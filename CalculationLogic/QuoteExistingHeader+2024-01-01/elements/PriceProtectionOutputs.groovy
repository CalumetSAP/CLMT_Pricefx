if (!quoteProcessor.isPostPhase()) return

final createOutput = libs.BdpLib.QuoteOutput
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def productMasterData = out.FindProductMasterData ?: [:]

def productMasterItem, configurator, dsData
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId

    configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)

    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    productMasterItem = productMasterData?.get(lnProduct.sku)

    // Price Protection Fields
    def exclusion
    def shipTo = dsData.ShipTo

    def exclusionSoldTo = out.FindExclusions ? out.FindExclusions[dsData?.SoldTo] ?: out.FindExclusions["*"] : null
    if (exclusionSoldTo) {
        exclusion = findExclusion(
                exclusionSoldTo,
                shipTo,
                productMasterItem?.PH1Code,
                productMasterItem?.Material
        )
    }

    quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
            lineItemOutputsConstants.PP_PRICE_PROTECTION_ID,
            lineItemOutputsConstants.PP_PRICE_PROTECTION_LABEL,
            "Price Protection",
            exclusion?.attribute2),
            null)
    )

    quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
            lineItemOutputsConstants.PP_NUMBER_OF_DAYS_ID,
            lineItemOutputsConstants.PP_NUMBER_OF_DAYS_LABEL,
            "Price Protection",
            exclusion?.attribute1),
            null)
    )

    quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
            lineItemOutputsConstants.PP_MOVEMENT_TIMING_ID,
            lineItemOutputsConstants.PP_MOVEMENT_TIMING_LABEL,
            "Price Protection",
            exclusion?.attribute3),
            null)
    )

    quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
            lineItemOutputsConstants.PP_MOVEMENT_START_ID,
            lineItemOutputsConstants.PP_MOVEMENT_START_LABEL,
            "Price Protection",
            exclusion?.attribute4),
            null)
    )

    quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
            lineItemOutputsConstants.PP_MOVEMENT_DAY_ID,
            lineItemOutputsConstants.PP_MOVEMENT_DAY_LABEL,
            "Price Protection",
            exclusion?.attribute5),
            null)
    )
}

return null

def findExclusion(exclusionSoldTo, shipToValue, ph1Value, materialValue) {
    def exclusion = null

    if (shipToValue) {
        def exclusionShipToSpecific = exclusionSoldTo[shipToValue]
        if (exclusionShipToSpecific) {
            exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToSpecific, ph1Value, materialValue)
        }
    }

    if (!exclusion) {
        def exclusionShipToNotSpecific = exclusionSoldTo["*"]
        if (exclusionShipToNotSpecific) {
            exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToNotSpecific, ph1Value, materialValue)
        }
    }

    return exclusion?.find()
}

def getExclusionStartingFromExclusionShipTo (exclusionShipTo, String ph1Value, String materialValue) {
    def exclusion = null
    def exclusionPH1Specific = exclusionShipTo[ph1Value]
    if (exclusionPH1Specific) {
        exclusion = exclusionPH1Specific[materialValue] ?: exclusionPH1Specific["*"]
    }
    if (!exclusion) {
        def exclusionPH1NotSpecific = exclusionShipTo["*"]
        if (exclusionPH1NotSpecific) {
            exclusion = exclusionPH1NotSpecific[materialValue] ?: exclusionPH1NotSpecific["*"]
        }
    }

    return exclusion
}