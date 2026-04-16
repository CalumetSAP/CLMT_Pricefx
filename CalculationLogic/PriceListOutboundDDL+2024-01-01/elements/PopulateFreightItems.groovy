import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

final pricelistConstants = libs.PricelistLib.Constants

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
SimpleDateFormat lastUpdateSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

def plId, plType

if (api.isDebugMode()) {
    plId = "879"
    plType = "RailFreightPriceMaintenance"
} else {
    def calcItem = dist?.calcItem
    plId = calcItem?.Key2
    plType = calcItem?.Value?.plType
}

if (plType != pricelistConstants.FREIGHT_MAINTENANCE_PL_TYPE && plType != pricelistConstants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE) return

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

def uuids = libs.QuoteLibrary.Calculations.getContractsUUIDs(plId)

Map quoteItemsGroupedBySAPContractNumber = getQuoteDSItems(getFiltersForPLsWithContract(plItems))?.groupBy { [it.SAPContractNumber] }

// ----- This is to get the max pricing date for each sap contract number between quotes and pl items ----- //
Map quotesMaxContractPricingDateMap = quoteItemsGroupedBySAPContractNumber?.collectEntries { key, values ->
    [(key) : values.find { it.ContractPricingDate }?.ContractPricingDate]
}
Map plsMaxContractPricingDateMap = plItems.groupBy {[it.ContractNumber] }.collectEntries { key, value ->
    [(key) : sdf.parse(value?.max { it.NewPriceValidFrom }?.NewPriceValidFrom)]
}

Map maxContractPricingDateMap = getMaxContractPricingDateMap(quotesMaxContractPricingDateMap, plsMaxContractPricingDateMap)
// ---------- //

def quoteItemsGroupedBySAPContractNumber2 = quoteItemsGroupedBySAPContractNumber?.collectEntries { key, values ->
    [(key) : values.groupBy { it.SAPContractNumber+"|"+it.SAPLineID }.collectEntries { key2, values2 ->
        [(key2) : values2.max { it.QuoteLastUpdate }]
    }]
}

def quoteItemsGroupedBySAPContractNumberAndDate = quoteItemsGroupedBySAPContractNumber?.collectEntries { key, values ->
    [(key) : values.groupBy { it.SAPContractNumber+"|"+it.SAPLineID+"|"+it.PriceValidFrom }.collectEntries { key2, values2 ->
        [(key2) : values2.max { it.QuoteLastUpdate }]
    }]
}

def itemsInfo = plItems.collect { plItem ->
    def qi = quoteItemsGroupedBySAPContractNumberAndDate?.get([plItem?.ContractNumber])?.get(plItem.ContractNumber + "|" + plItem.ContractLine + "|" + plItem.OldPriceValidFrom)

    [
            plItem : plItem,
            key    : qi?.QuoteID + "|" + qi?.LineID,
            date   : plItem.NewPriceValidFrom,
            future : plItem.FutureDate == "Y"
    ]
}

def futureGroups = itemsInfo
        .findAll { it.future }
        .groupBy { it.key }

def futureOrdinalByPlItem = [:]

futureGroups.each { key, list ->
    list.sort { it.date }
    list.eachWithIndex { item, idx ->
        futureOrdinalByPlItem[item.plItem] = idx + 1
    }
}

Date baseDate = lastUpdateSdf.parse(priceList.lastUpdateDate as String)

def plItemsSorted = plItems.sort { it.NewPriceValidFrom }

Map<String, Integer> counterPerContractLine = [:].withDefault { 0 }

Calendar cal = Calendar.getInstance()

def quoteItemToUpdate, contractKey, minutesToAdd, updatedbyID, uuid, quoteExtendItemToUpdate, extendOldPrice, extendOldFreight
for (plItem in plItemsSorted) {
    contractKey = plItem.ContractNumber + "|" + plItem.ContractLine
    quoteItemToUpdate = quoteItemsGroupedBySAPContractNumberAndDate?.get([plItem.ContractNumber])?.get(contractKey + "|" + plItem.OldPriceValidFrom)
    if (quoteItemToUpdate) {
        uuid = uuids[plItem?.ContractNumber]
        updatedbyID = buildUpdatedByID(priceList.id, (plItem.FutureDate == "Y"), futureOrdinalByPlItem[plItem])
        quoteItemToUpdate.lastUpdateDate = null
        quoteItemToUpdate.uuid = uuid

        extendOldPrice = plItem.ExtendOldRecord?.contains("Price")
        extendOldFreight = plItem.ExtendOldRecord?.contains("Freight")
        if (extendOldPrice || extendOldFreight) {
            minutesToAdd = counterPerContractLine[contractKey]

            cal.setTime(baseDate)
            cal.add(Calendar.MINUTE, minutesToAdd)

            counterPerContractLine[contractKey]++

            quoteExtendItemToUpdate = quoteItemToUpdate.clone()
            if (extendOldPrice) {
                quoteExtendItemToUpdate.PriceValidTo = sdf.format(sdf.parse(plItem.NewPriceValidFrom)-1)
            }
            if (extendOldFreight) {
                quoteExtendItemToUpdate.FreightValidTo = sdf.format(sdf.parse(plItem.NewFreightValidFrom)-1)
            }
            quoteExtendItemToUpdate.QuoteLastUpdate = lastUpdateSdf.format(cal.getTime())
            quoteExtendItemToUpdate.UpdatedbyProcess = "Freight Price Maintenance - Extended"
            quoteExtendItemToUpdate.UpdatedbyID = updatedbyID+"Ext"

            api.isDebugMode() ? loader.add(quoteExtendItemToUpdate) : loader.addRow(quoteExtendItemToUpdate)
        }

        minutesToAdd = counterPerContractLine[contractKey]

        cal.setTime(baseDate)
        cal.add(Calendar.MINUTE, minutesToAdd)

        counterPerContractLine[contractKey]++

        quoteItemToUpdate.Adder = plItem.NewAdder
        quoteItemToUpdate.Price = plItem.NewProductPrice
        quoteItemToUpdate.DeliveredPrice = plItem.NewDeliveredPrice
        quoteItemToUpdate.FreightAmount = plItem.NewFreightAmount
        quoteItemToUpdate.PriceValidFrom = plItem.NewPriceValidFrom
        quoteItemToUpdate.PriceValidTo = plItem.NewPriceValidTo
        quoteItemToUpdate.FreightValidFrom = plItem.NewFreightValidFrom
        quoteItemToUpdate.FreightValidto = plItem.NewFreightValidTo
        quoteItemToUpdate.QuoteLastUpdate = lastUpdateSdf.format(cal.getTime())
        quoteItemToUpdate.LastUpdatebyName = priceList.lastUpdateByName
        quoteItemToUpdate.ContractPricingDate = maxContractPricingDateMap[[plItem.ContractNumber]]
        quoteItemToUpdate.UpdatedbyProcess = "Freight Price Maintenance"
        quoteItemToUpdate.UpdatedbyID = updatedbyID
        quoteItemToUpdate.SoldtoName = plItem.SoldToName ?: getSoldToName(plItem.SoldToNumber)

        api.isDebugMode() ? loader.add(quoteItemToUpdate) : loader.addRow(quoteItemToUpdate)
    }
}

// Update max pricing date for existing items
quoteItemsGroupedBySAPContractNumber2.remove([null])

def maxContractPricingDate
quoteItemsGroupedBySAPContractNumber2.each { sapContractNumber, value1 ->
    maxContractPricingDate = maxContractPricingDateMap[sapContractNumber]
    value1.each { key2, quote ->
        quote.ContractPricingDate = maxContractPricingDate
        api.isDebugMode() ? loader.add(quote) : loader.addRow(quote)
    }
}

return loader

def buildUpdatedByID(id, isFutureLine, ordinal) {
    if (!isFutureLine) return id

    return id + "F-" + (ordinal ?: 1)
}

def getFiltersForPLsWithContract (plItems) {
    Set<String> contractNumbers = new HashSet<>()
    Set<String> contractLines = new HashSet<>()
    for (plItem in plItems) {
        if (plItem.ContractNumber) contractNumbers.add(plItem.ContractNumber)
        if (plItem.ContractLine) contractLines.add(plItem.ContractLine)
    }

    def keyFilters = []
    if (contractNumbers) {
        keyFilters.add(Filter.in("SAPContractNumber", contractNumbers))
    }
    if (contractLines) {
        keyFilters.add(Filter.in("SAPLineID", contractLines))
    }

    if (!keyFilters) return Filter.equal("SAPContractNumber", "Nothing")

    return Filter.and(*keyFilters)
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

def getSoldToName(soldTo) {
    def customerFields = ["customerId", "name"]
    def customerFilter = Filter.equal("customerId", soldTo)

    def selectedCustomer = api.find("C", 0, 1, null, customerFields, customerFilter)?.find()

    return selectedCustomer?.name
}