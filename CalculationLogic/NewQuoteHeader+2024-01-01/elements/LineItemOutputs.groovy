if (!quoteProcessor.isPostPhase()) return

final createOutput = libs.BdpLib.QuoteOutput
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def isSoldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID) as Boolean

def productMasterData = out.FindProductMasterData ?: [:]
def existingContracts = out.FindExistingContracts ?: [:]

def productMasterItem

def configurator, shipTo, shipToOutput
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    productMasterItem = productMasterData?.get(lnProduct.sku)

    configurator = calculations.getInputValue(lnProduct, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)

    shipTo = out.FindCustomerShipTo?.size() == 1 ? out.FindCustomerShipTo?.find()?.split(" - ")?.getAt(0) : configurator?.get(lineItemConstants.SHIP_TO_ID)?.split(" - ")?.getAt(0)
    shipToOutput = out.FindCustomerShipTo?.size() == 1 ? out.FindCustomerShipTo?.find() : configurator?.get(lineItemConstants.SHIP_TO_ID)
    if (!shipTo) shipTo = ""

    quoteProcessor.with {
        quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
                lineItemOutputsConstants.SAP_CONTRACT_ID,
                lineItemOutputsConstants.SAP_CONTRACT_LABEL,
                null,
                existingContracts?.get(shipTo)),
                null)
        )

        quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
                lineItemOutputsConstants.MATERIAL_PACKAGE_STYLE_ID,
                lineItemOutputsConstants.MATERIAL_PACKAGE_STYLE_LABEL,
                null,
                productMasterItem?.MaterialPackageStyle),
                null)
        )

        quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
                lineItemOutputsConstants.CONTAINER_DESCRIPTION_ID,
                lineItemOutputsConstants.CONTAINER_DESCRIPTION_LABEL,
                null,
                productMasterItem?.ContainerDescription),
                null)
        )

        quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.numeric(
                lineItemOutputsConstants.WEIGHT_PER_GALLON_ID,
                lineItemOutputsConstants.WEIGHT_PER_GALLON_LABEL,
                null,
                productMasterItem?.NetWeight),
                null)
        )

        if (!isSoldToOnly) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.addUserGroup(createOutput.text(
                    lineItemOutputsConstants.SHIP_TO_ID,
                    lineItemOutputsConstants.SHIP_TO_LABEL,
                    null,
                    shipToOutput as String),
                    null)
            )
        }

        // Price Protection Fields
        def exclusion

        def exclusionSoldTo = out.FindExclusions
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
}

return null

def updateInputValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}

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