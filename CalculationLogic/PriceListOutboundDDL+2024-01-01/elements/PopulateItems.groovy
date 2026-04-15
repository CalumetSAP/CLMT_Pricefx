import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

final roundingUtils = libs.SharedLib.RoundingUtils
final pricelistConstants = libs.PricelistLib.Constants

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def plId, plType

if (api.isDebugMode()) {
    plId = "879"
    plType = "RailFreightPriceMaintenance"
} else {
    def calcItem = dist?.calcItem
    plId = calcItem?.Key2
    plType = calcItem?.Value?.plType
}

if (plType == pricelistConstants.FREIGHT_MAINTENANCE_PL_TYPE || plType == pricelistConstants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE) return

loader = api.isDebugMode() ? [] : dist?.dataLoader

def attributeMappingFilters = [
        Filter.equal("pricelistId", plId),
        Filter.isNotNull("elementName")
]
HashMap<String, String> attributeMapping = api.stream("PLIM", null, ["fieldName", "elementName"], *attributeMappingFilters).withCloseable {
    it.collectEntries {
        [(it.fieldName): it.elementName]
    }
}
attributeMapping["sku"] = "Material"

List<Map> plItems = api.stream("XPLI", null, Filter.equal("pricelistId", plId)).withCloseable {
    it.collect { plItem ->
        attributeMapping.collectEntries {fieldName, elementName ->
            [(elementName): plItem[fieldName]]
        }
    }
}

Map priceList = getPriceList(plId)
if (plType == pricelistConstants.PRICE_LIST_ZBPL_PL_TYPE) {
    def quotes = getQuoteDSItems(getFiltersForPLsWithNoContract(plItems))
    def quotesOrdered = getQuoteDSItemsOrderByLastUpdateDate(getFiltersForPLsWithNoContract(plItems))
    Map quoteItemsGroupedBySAPContractNumber = quotes?.groupBy { [it.SAPContractNumber] }
    Map quoteItemsGroupedByMaterialAndPLNumber = quotesOrdered?.groupBy { [it.SalesOrg, it.Material, it.PriceListPLT] }

    // ----- This is to get the max pricing date for each sap contract number between quotes and pl items ----- //
    Map quotesMaxContractPricingDateMap = quoteItemsGroupedBySAPContractNumber?.collectEntries { key, values ->
        [(key) : values.find { it.ContractPricingDate }?.ContractPricingDate]
    }

    def plItemsWithContract = []
    for (plItem in plItems) {
        quoteItemsGroupedByMaterialAndPLNumber[plItem.SalesOrg, plItem.Material, plItem.PricelistNumber]?.each {
            plItemsWithContract.add([
                    SAPContractNumber   : it.SAPContractNumber,
                    NewEffectiveDate    : plItem.NewEffectiveDate
            ])
        }
    }
    Map plsMaxContractPricingDateMap = plItemsWithContract.groupBy { [it.SAPContractNumber] }.collectEntries { key, value ->
        [(key) : sdf.parse(value?.max { it.NewEffectiveDate }?.NewEffectiveDate)]
    }

    Map maxContractPricingDateMap = getMaxContractPricingDateMap(quotesMaxContractPricingDateMap, plsMaxContractPricingDateMap)

    def quoteItemToUpdate, sapContract, uuid, contractAndLineKey
    Map contractsUUIDs = [:]
    def contractAndLinesUpdated = []
    for (plItem in plItems) {
        quoteItemsGroupedByMaterialAndPLNumber[plItem.SalesOrg, plItem.Material, plItem.PricelistNumber]?.each {
            contractAndLineKey = it.SAPContractNumber + "|" + it.SAPLineID
            if (contractAndLinesUpdated.contains(contractAndLineKey)) return
            contractAndLinesUpdated.add(contractAndLineKey)

            quoteItemToUpdate = it
            sapContract = quoteItemToUpdate.SAPContractNumber
            quoteItemToUpdate.DeliveredPrice = libs.SharedLib.RoundingUtils.round(getNewDeliveredPrice(plItem.NewBasePrice, quoteItemToUpdate.DeliveredPrice, quoteItemToUpdate.Price), 2)
            quoteItemToUpdate.lastUpdateDate = null
            quoteItemToUpdate.Per = plItem.Per
            quoteItemToUpdate.Price = libs.SharedLib.RoundingUtils.round(plItem.NewBasePrice, 2)
            quoteItemToUpdate.PricingUOM = plItem.BaseUOM
            quoteItemToUpdate.Cost = plItem.Cost
            quoteItemToUpdate.Currency = plItem.Currency
            quoteItemToUpdate.PriceValidFrom = plItem.NewEffectiveDate
            quoteItemToUpdate.PriceValidTo = plItem.NewExpirationDate
            quoteItemToUpdate.SoldtoName = it.SoldtoName ?: getSoldToName(it.SoldTo)
            quoteItemToUpdate.QuoteLastUpdate = priceList.lastUpdateDate
            quoteItemToUpdate.LastUpdatebyName = priceList.lastUpdateByName
            quoteItemToUpdate.ContractPricingDate = maxContractPricingDateMap[[sapContract]]
            quoteItemToUpdate.UpdatedbyProcess = "Pricelist - ZBPL"
            quoteItemToUpdate.UpdatedbyID = priceList.id

            uuid = contractsUUIDs.getOrDefault(sapContract, api.uuid())
            contractsUUIDs.putIfAbsent(sapContract, uuid)
            quoteItemToUpdate.uuid = uuid

            loader.addRow(quoteItemToUpdate)
        }
    }

    // Update max pricing date for existing items
    def maxContractPricingDate
    quoteItemsGroupedBySAPContractNumber.each { sapContractNumber, rows ->
        maxContractPricingDate = maxContractPricingDateMap[sapContractNumber]
        rows.each {
            it.ContractPricingDate = maxContractPricingDate
            loader.addRow(it)
        }
    }
} else {
    def uuids = libs.QuoteLibrary.Calculations.getContractsUUIDs(plId)
    Map quoteItemsGroupedBySAPContractNumber = getQuoteDSItems(getFiltersForPLsWithContract(plItems))?.groupBy { [it.SAPContractNumber] }

    // ----- This is to get the max pricing date for each sap contract number between quotes and pl items ----- //
    Map quotesMaxContractPricingDateMap = quoteItemsGroupedBySAPContractNumber?.collectEntries { key, values ->
        [(key) : values.find { it.ContractPricingDate }?.ContractPricingDate]
    }
    Map plsMaxContractPricingDateMap = plItems.groupBy {[it.Contract] }.collectEntries { key, value ->
        [(key) : sdf.parse(value?.max { it.EffectiveDate }?.EffectiveDate)]
    }

    Map maxContractPricingDateMap = getMaxContractPricingDateMap(quotesMaxContractPricingDateMap, plsMaxContractPricingDateMap)
    // ---------- //

    quoteItemsGroupedBySAPContractNumber = quoteItemsGroupedBySAPContractNumber?.collectEntries { key, values ->
        [(key) : values.groupBy { it.QuoteID+"|"+it.LineID+"|"+it.UpdatedbyID }.collectEntries { key2, values2 ->
            [(key2) : values2.find()]
        }]
    }

    if (plType == pricelistConstants.PRICING_FORMULA_PL_TYPE) {
        Map referencePeriodMap = getReferencePeriodMap()
        def quoteItemToUpdate, uuid
        for (plItem in plItems) {
            quoteItemToUpdate = quoteItemsGroupedBySAPContractNumber?.get([plItem?.Contract])?.get(plItem.QuoteDSKeys)
            if (quoteItemToUpdate) {
                uuid = uuids[plItem?.Contract]
                quoteItemToUpdate.DeliveredPrice = getNewDeliveredPrice(plItem.NewPrice, quoteItemToUpdate.DeliveredPrice, quoteItemToUpdate.Price)
                quoteItemToUpdate = updateRowCommonFields(quoteItemToUpdate, plItem, priceList, maxContractPricingDateMap[[plItem.Contract]], "Pricing Formula", uuid)
                quoteItemToUpdate.IndexNumberOne = plItem.Index1
                quoteItemToUpdate.IndexNumberTwo = plItem.Index2
                quoteItemToUpdate.IndexNumberThree = plItem.Index3
                quoteItemToUpdate.IndexNumberOnePercent = roundingUtils.round(plItem.Index1Percent, 2) ?: BigDecimal.ZERO
                quoteItemToUpdate.IndexNumberTwoPercent = roundingUtils.round(plItem.Index2Percent, 2) ?: BigDecimal.ZERO
                quoteItemToUpdate.IndexNumberThreePercent = roundingUtils.round(plItem.Index3Percent, 2) ?: BigDecimal.ZERO
                quoteItemToUpdate.Adder = plItem.Adder
                quoteItemToUpdate.AdderUOM = plItem.AdderUOM
                quoteItemToUpdate.ReferencePeriod = referencePeriodMap[plItem.ReferencePeriod]
                quoteItemToUpdate.ReferencePeriodValue = plItem.ReferencePeriod
                quoteItemToUpdate.SoldtoName = plItem.SoldToName ?: getSoldToName(plItem.SoldTo)

                loader.addRow(quoteItemToUpdate)
            }
        }
    } else if (plType == pricelistConstants.MASS_EDIT_PL_TYPE) {
        def quoteItemToUpdate, uuid
        for (plItem in plItems) {
            quoteItemToUpdate = quoteItemsGroupedBySAPContractNumber?.get([plItem?.Contract])?.get(plItem.QuoteDSKeys)
            if (quoteItemToUpdate) {
                uuid = uuids[plItem?.Contract]
                quoteItemToUpdate.DeliveredPrice = getNewDeliveredPrice(plItem.NewPrice, quoteItemToUpdate.DeliveredPrice, quoteItemToUpdate.Price)
                quoteItemToUpdate = updateRowCommonFields(quoteItemToUpdate, plItem, priceList, maxContractPricingDateMap[[plItem.Contract]], "Mass Edit", uuid)
                quoteItemToUpdate.SoldtoName = plItem.SoldToName ?: getSoldToName(plItem.SoldTo)
                quoteItemToUpdate.PriceChangeFlag = true

                loader.addRow(quoteItemToUpdate)
            }
        }
    }
    // Update max pricing date for existing items
    quoteItemsGroupedBySAPContractNumber.remove([null])

    def maxContractPricingDate
    quoteItemsGroupedBySAPContractNumber.each { sapContractNumber, value1 ->
        maxContractPricingDate = maxContractPricingDateMap[sapContractNumber]
        value1.each { key2, quote ->
            quote.ContractPricingDate = maxContractPricingDate
            loader.addRow(quote)
        }
    }
}

//api.trace("loader", loader)

def getFiltersForPLsWithNoContract (plItems) {
    List<String> salesOrg = plItems?.SalesOrg?.unique() ?: []
    List<String> materials = plItems?.Material?.unique() ?: []
    List<String> priceListNumbers = plItems?.PricelistNumber?.unique() ?: []

    return Filter.and(
            Filter.equal("PriceType", "3"),
            Filter.in("SalesOrg", salesOrg),
            Filter.in("Material", materials),
            Filter.in("PriceListPLT", priceListNumbers),
            Filter.notEqual("SAPContractNumber", ""),
            Filter.isNotNull("SAPContractNumber")
    )
}

def getFiltersForPLsWithContract (plItems) {
    Set<String> quoteIdKeys = new HashSet<>()
    Set<String> lineIDKeys = new HashSet<>()
    Set<String> updatedByIDKeys = new HashSet<>()
    def keys
    def updatedByID
    for (quoteDSKey in plItems.QuoteDSKeys) {
        keys = quoteDSKey?.split("\\|")
        if (keys) {
            updatedByID = keys[2]

            quoteIdKeys.add(keys[0])
            lineIDKeys.add(keys[1])
            updatedByIDKeys.add(updatedByID == "null" ? null : updatedByID)
        }
    }
    updatedByIDKeys.remove("")

    def keyFilters = []
    if (quoteIdKeys) {
        keyFilters.add(Filter.in("QuoteID", quoteIdKeys))
    }
    if (lineIDKeys) {
        keyFilters.add(Filter.in("LineID", lineIDKeys))
    }
    if (updatedByIDKeys) {
        keyFilters.add(Filter.in("UpdatedbyID", updatedByIDKeys))
    }

    def plsSAPContractNumbers = plItems?.Contract?.unique() ?: []

    if (keyFilters) {
        return Filter.or(
                Filter.and(*keyFilters),
                Filter.in("SAPContractNumber", plsSAPContractNumbers)
        )
    }

    return Filter.in("SAPContractNumber", plsSAPContractNumbers)
}

def getQuoteDSItems (filters) {
    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def query = ctx.newQuery(ds, false)
    query = query
            .selectAll(true)
            .setUseCache(false)
            .where(filters)
            .orderBy("ContractPricingDate DESC")

    return ctx.executeQuery(query)?.getData()
}

def getQuoteDSItemsOrderByLastUpdateDate (filters) {
    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def query = ctx.newQuery(ds, false)
    query = query
            .selectAll(true)
            .setUseCache(false)
            .where(filters)
            .orderBy("QuoteLastUpdate DESC")

    return ctx.executeQuery(query)?.getData()
}

Map getMaxContractPricingDateMap (quotesMaxContractPricingDateMap, plsMaxContractPricingDateMap) {
    Map maxContractPricingDateMap = new HashMap()
    def plsMaxContractPricingDateMapValue
    quotesMaxContractPricingDateMap.each { key, value ->
        plsMaxContractPricingDateMapValue = plsMaxContractPricingDateMap.remove(key)
        if (plsMaxContractPricingDateMapValue > value) {
            maxContractPricingDateMap[key] = plsMaxContractPricingDateMapValue
        } else {
            maxContractPricingDateMap[key] = value
        }
    }
    plsMaxContractPricingDateMap?.each { key, value ->
        maxContractPricingDateMap[key] = value
    }

    return maxContractPricingDateMap
}

Map getPriceList (String plId) {
    return api.find("PL", 0, 1, null, Filter.equal("id", plId))?.find()
}

//TODO move to lib
Map getReferencePeriodMap () {
    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.equal("key2", "ReferencePeriod")
    ]

    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.DROPDOWN_OPTIONS, ["key3", "attribute1"], "key3", *filters)?.collectEntries { [(it.attribute1): it.key3] }
}

def updateRowCommonFields (quoteItemToUpdate, plItem, priceList, contractPricingDate, updatedByProcess, uuid) {
    quoteItemToUpdate.lastUpdateDate = null
    quoteItemToUpdate.Price = plItem.NewPrice
    quoteItemToUpdate.PriceValidFrom = plItem.EffectiveDate
    quoteItemToUpdate.PriceValidTo = plItem.ExpirationDate
    quoteItemToUpdate.QuoteLastUpdate = priceList.lastUpdateDate
    quoteItemToUpdate.LastUpdatebyName = priceList.lastUpdateByName
    quoteItemToUpdate.ContractPricingDate = contractPricingDate
    quoteItemToUpdate.UpdatedbyProcess = updatedByProcess
    quoteItemToUpdate.UpdatedbyID = priceList.id
    quoteItemToUpdate.uuid = uuid

    return quoteItemToUpdate
}

def getNewDeliveredPrice (newPrice, oldDeliveredPrice, oldPrice) {
    if (newPrice != null && oldDeliveredPrice != null && oldPrice != null) {
        return newPrice + (oldDeliveredPrice - oldPrice)
    }
    return null
}

def getSoldToName(soldTo) {
    def customerFields = ["customerId", "name"]
    def customerFilter = Filter.equal("customerId", soldTo)

    def selectedCustomer = api.find("C", 0, 1, null, customerFields, customerFilter)?.find()

    return selectedCustomer?.name
}