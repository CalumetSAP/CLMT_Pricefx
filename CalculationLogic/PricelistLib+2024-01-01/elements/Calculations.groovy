import groovy.transform.Field

@Field final String APPROVED_PRICE_LIST_STATUS_CPT = "ApprovedPriceListStatus"
@Field final String APPROVED_PRICE_LIST_STATUS_FOR_SCALES_CPT = "ApprovedPriceListStatusForScales"
@Field final String APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT = "ApprovedPriceListStatusForBasePricing"
@Field final String PENDING_STATUS = "PENDING"
@Field final String PROCESSING_STATUS = "PROCESSING"
@Field final String PENDING_CUSTOM_EVENT = "PENDING CUSTOM EVENT"
@Field final String READY_STATUS = "READY"
@Field final String ERROR_STATUS = "ERROR"

def setReadyStatus (priceListId) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_CPT)
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, priceListId, READY_STATUS))
}

def setPendingCustomEventStatus (List priceListId) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_CPT)
    priceListId.each { plId ->
        api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, plId, PENDING_CUSTOM_EVENT))
    }
}

def setErrorStatus (List priceListId) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_CPT)
    priceListId.each { plId ->
        api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, plId, ERROR_STATUS))
    }
}

def setProcessingStatus (priceListId) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_CPT)
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, priceListId, PROCESSING_STATUS))
}

def setPendingStatus (priceListId, priceListType) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_CPT)
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, priceListId, PENDING_STATUS, priceListType))
}

def addOrUpdatePriceListForScalesStatusToPending (priceListId) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_FOR_SCALES_CPT)
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, priceListId, PENDING_STATUS))
}

def addOrUpdatePriceListForBasePricingStatusToPending (priceListId) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT)
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, priceListId, PENDING_STATUS))
}

def addOrUpdatePriceListForBasePricingStatusToProcessing (priceListIds) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT)
    priceListIds.each { plId ->
        api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, plId, PROCESSING_STATUS))
    }
}

def addOrUpdatePriceListForBasePricingStatusToReady (List priceListIds) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT)
    priceListIds.each { plId ->
        api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, plId, READY_STATUS))
    }
}

def addOrUpdatePriceListForBasePricingStatusToError (List priceListIds) {
    def cptData = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT)
    priceListIds.each { plId ->
        api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, plId, ERROR_STATUS))
    }
}

def addOrUpdatePriceListForScalesStatusToReadyUsingBoundCall(List priceListIds) {
    def cptId = api.findLookupTable(APPROVED_PRICE_LIST_STATUS_FOR_SCALES_CPT)?.id
    if (cptId) {
        def readyBody = [
                "data": [
                        "attribute1": READY_STATUS
                ],
                "operation": "update"
        ]
        def oldValue
        for (plId in priceListIds) {
            oldValue = api.findLookupTableValues(APPROVED_PRICE_LIST_STATUS_FOR_SCALES_CPT, Filter.equal("name", plId)).find()
            if (oldValue) {
                readyBody.data.typedId = oldValue.typedId
                readyBody.oldValues = oldValue
                api.boundCall("SystemUpdate", "/lookuptablemanager.update/${cptId.toString()}", api.jsonEncode(readyBody).toString(), false)
            }
        }
    }
}


private def buildRowToAddOrUpdate (cptData, priceListId, status, priceListType = null) {
    def cptRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "name"              : priceListId,
            "attribute1"        : status,
    ]
    if (priceListType) {
        cptRow.attribute2 = priceListType
    }

    return cptRow
}

def getPendingRowsForQuoteDS () {
    return getCPTRowsByStatusAndPlTypes(
            PENDING_STATUS,
            [
                    libs.PricelistLib.Constants.PRICING_FORMULA_PL_TYPE,
                    libs.PricelistLib.Constants.MASS_EDIT_PL_TYPE,
                    libs.PricelistLib.Constants.PRICE_LIST_ZBPL_PL_TYPE,
                    libs.PricelistLib.Constants.FREIGHT_MAINTENANCE_PL_TYPE,
                    libs.PricelistLib.Constants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE
            ]
    )
}

def getProcessingRowsForQuoteDS () {
    return getCPTRowsByStatusAndPlTypes(
            PROCESSING_STATUS,
            [
                    libs.PricelistLib.Constants.PRICING_FORMULA_PL_TYPE,
                    libs.PricelistLib.Constants.MASS_EDIT_PL_TYPE,
                    libs.PricelistLib.Constants.PRICE_LIST_ZBPL_PL_TYPE,
                    libs.PricelistLib.Constants.FREIGHT_MAINTENANCE_PL_TYPE,
                    libs.PricelistLib.Constants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE
            ]
    )
}

def getPendingCustomEventPriceListIds(Filter lastUpdateDateFilter) {
    return getCPTIdsByStatusAndExtraFilter(APPROVED_PRICE_LIST_STATUS_CPT, PENDING_CUSTOM_EVENT, lastUpdateDateFilter)
}

def getPendingPriceListIdsForScales() {
    return getCPTIdsByStatus(APPROVED_PRICE_LIST_STATUS_FOR_SCALES_CPT, PENDING_STATUS)
}

def getPendingPriceListIdsForBasePricing() {
    return getCPTIdsByStatus(APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT, PENDING_STATUS)
}

def getProcessingPriceListIdsForBasePricing() {
    return getCPTIdsByStatus(APPROVED_PRICE_LIST_STATUS_FOR_BASE_PRICING_CPT, PROCESSING_STATUS)
}

private def getCPTIdsByStatus (String cptName, String status) {
    return api.findLookupTableValues(cptName, "lastUpdateDate", Filter.equal("attribute1", status))?.collect {
        it.name
    }
}

private def getCPTIdsByStatusAndExtraFilter (String cptName, String status, Filter extraFilter) {
    def filters = [
            Filter.equal("attribute1", status),
            extraFilter
    ]
    return api.findLookupTableValues(cptName, "lastUpdateDate", *filters)?.collect {
        it.name
    }
}

def getCPTRowsByStatusAndPlTypes (String status, List<String> plTypes) {
    List filters = [
            Filter.equal("attribute1", status),
            Filter.in("attribute2", plTypes)
    ]
    return api.findLookupTableValues(APPROVED_PRICE_LIST_STATUS_CPT, "lastUpdateDate", *filters)?.collect {
        [
                plId    : it.name,
                plType  : it.attribute2
        ]
    }
}

List<String> getUUIDsById (id) {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID, ["key2", "attribute1"], null, Filter.equal("key1", id)).attribute1
}

BigDecimal getRoundedValueBasedOnUOM (BigDecimal value, String targetUOM) {
    return libs.SharedLib.RoundingUtils.round(value, libs.PricelistLib.Constants.getUOMRoundingDecimals(targetUOM))
}

BigDecimal calculatePercent (baseValue, finalValue) {
    if (finalValue && baseValue != null) {
        return 1 - (baseValue/finalValue)
    }
    return 0
}

BigDecimal calculateInversePercent (baseValue, finalValue) {
    if (baseValue && finalValue != null) {
        return 1 - (finalValue/baseValue)
    }
    return 0
}

BigDecimal getNewPrice (price, isPriceChangePerUOM, isPriceChangePercent, priceChangePerUOM, selectedUOM, pricingUOM, priceChangePercent, material, uomConversions, globalUomConversion, numberOfDecimals = null) {
    BigDecimal newPrice = null
    if (price) {
        if (isPriceChangePerUOM) {
            BigDecimal priceChange = priceChangePerUOM
            if (priceChange == BigDecimal.ZERO) {
                newPrice = price
            } else {
                if (priceChange && selectedUOM && pricingUOM && selectedUOM != pricingUOM) {//convert priceChange from selectedUOM to pricingUOM
                    BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, selectedUOM, pricingUOM, uomConversions, globalUomConversion)?.toBigDecimal()
                    if (conversionFactor) {
                        priceChange = priceChange * conversionFactor
                    } else {
                        api.criticalAlert("Missing conversion from Input UOM (${selectedUOM}) to Price UOM (${pricingUOM}) for material ${material}")
                        priceChange = null
                    }
                }
                newPrice = priceChange != null ? price + priceChange : null
            }
        } else if (isPriceChangePercent) {
            newPrice = priceChangePercent != null ? price * (1 + priceChangePercent) : null
        }
    }

    return numberOfDecimals ? libs.SharedLib.RoundingUtils.round(newPrice, numberOfDecimals) : newPrice
}

def getCostAverage (List costPXRows) {
    def rowsFiltered = costPXRows.findAll { it.attribute9.toBigDecimal() || (it.attribute5 && it.attribute5.replaceAll(",", "").toBigDecimal() > BigDecimal.ZERO) }
    def size = rowsFiltered.size()
    if (size) {
        def costSum = rowsFiltered.sum { (it.attribute9.toBigDecimal() ?: it.attribute5.replaceAll(",", "").toBigDecimal()) / it.attribute3.replaceAll(",", "").toBigDecimal() }
        return costSum/size
    }
    return null
}