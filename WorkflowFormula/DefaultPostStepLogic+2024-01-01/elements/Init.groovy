if (workflowHistory.steps) {
    if (workflowHistory.steps.last().uniqueName == workflowHistory.activeStep.uniqueName) {
        if (workflowHistory.activeStep.approved) {
            fillCPTs(pricelist?.id, pricelist?.headerTypeUniqueName)
        }
    }
} else {
    fillCPTs(pricelist?.id, pricelist?.headerTypeUniqueName)
}

def fillCPTs (plId, plType) {
    if (plType == libs.PricelistLib.Constants.PRICE_LIST_ZBPL_PL_TYPE) {
        libs.PricelistLib.Calculations.setPendingStatus(plId, plType)
        libs.PricelistLib.Calculations.addOrUpdatePriceListForBasePricingStatusToPending(plId)
    } else {
        def contracts = new HashSet()
        def start = 0
        def max = api.getMaxFindResultsLimit()
        def someContracts

        Boolean isFreightPL = plType == libs.PricelistLib.Constants.FREIGHT_MAINTENANCE_PL_TYPE || plType == libs.PricelistLib.Constants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE
        def contractElementName = isFreightPL ? "ContractNumber" : "Contract"

        String contractFieldName = api.find("PLIM", 0, 1, null, ["fieldName"], Filter.equal("pricelistId", plId), Filter.equal("elementName", contractElementName))?.find()?.fieldName
        if (contractFieldName) {
            while (someContracts = api.find("XPLI", start, max, null, [contractFieldName], true, Filter.equal("pricelistId", plId))) {
                for (newContract in someContracts) {
                    contracts.add(newContract?.get(contractFieldName))
                }
                start += max
            }
        }
        contracts.remove(null)
        contracts.remove("")

        for (contract in contracts) {
            libs.QuoteLibrary.Calculations.addContractUUID(plId, contract)
        }
        libs.PricelistLib.Calculations.setPendingStatus(plId, plType)
        if (plType == libs.PricelistLib.Constants.MASS_EDIT_PL_TYPE || isFreightPL) {
            libs.PricelistLib.Calculations.addOrUpdatePriceListForScalesStatusToPending(plId)
        }
    }
}