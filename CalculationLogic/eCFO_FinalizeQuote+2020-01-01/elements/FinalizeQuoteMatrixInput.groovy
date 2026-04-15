if (customFormProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
def lineItems = api.getCalculableLineItemCollection(api.currentItem().parentTypedId)?.lineItems
def configurator
def matrixValues = lineItems?.collect { li ->
    configurator = li.inputs.find { it.name == lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME }?.value
    [
            "typedId"                                           : li.typedId,
            (lineItemConstants.MATERIAL_LABEL)                  : li.sku,
            (lineItemConstants.SHIP_TO_LABEL)                   : findOutput(li, lineItemOutputsConstants.SHIP_TO_ID),
            (lineItemConstants.PLANT_LABEL)                     : findOutput(li, lineItemOutputsConstants.PLANT_ID),
            (lineItemConstants.INCO_TERM_LABEL)                 : out.LoadIncoTerm?.get(configurator?.get(lineItemConstants.INCO_TERM_ID)),
            (lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL)   : out.LoadMeansOfTransportation?.get(configurator?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_ID))
    ]
}

Map<String, Object> finalizeQuoteMatrix = customFormProcessor.getHelper().getRoot().getInputByName("FinalizeQuoteMatrix")
if (finalizeQuoteMatrix) {
    def actualMatrixValues = finalizeQuoteMatrix?.get("value")?.groupBy { it.typedId } ?: [:]
    def newMatrixValues = []
    def actualMatrix
    matrixValues.each { matrixValue ->
        actualMatrix = actualMatrixValues?.get(matrixValue.typedId)?.find()
        if (actualMatrix) {
            newMatrixValues.add([
                    "typedId"                                           : matrixValue.typedId,
                    (lineItemConstants.MATERIAL_LABEL)                  : matrixValue?.get(lineItemConstants.MATERIAL_LABEL),
                    (lineItemConstants.SHIP_TO_LABEL)                   : matrixValue?.get(lineItemConstants.SHIP_TO_LABEL),
                    (lineItemConstants.PLANT_LABEL)                     : matrixValue?.get(lineItemConstants.PLANT_LABEL),
                    (lineItemConstants.INCO_TERM_LABEL)                 : actualMatrix?.get(lineItemConstants.INCO_TERM_LABEL) ?: matrixValue?.get(lineItemConstants.INCO_TERM_LABEL),
                    (lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL)   : actualMatrix?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL) ?: matrixValue?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL)
            ])
        } else {
            newMatrixValues.add(matrixValue)
        }
    }
    finalizeQuoteMatrix.value = newMatrixValues
    customFormProcessor.addOrUpdateInput(finalizeQuoteMatrix)
} else {
    def incoTermOptions = out.LoadIncoTerm ? (out.LoadIncoTerm as Map).values() : []
    def meansOfTransportationOptions = out.LoadMeansOfTransportation ? (out.LoadMeansOfTransportation as Map).values() : []

    def param = api.inputBuilderFactory()
            .createInputMatrix("FinalizeQuoteMatrix")
            .setLabel("Items")
            .setNoRefresh(true)
            .setHideAddButton(true)
            .setHideRemoveButton(true)
            .setColumns([
                    "typedId",
                    lineItemConstants.MATERIAL_LABEL,
                    lineItemConstants.SHIP_TO_LABEL,
                    lineItemConstants.PLANT_LABEL,
                    lineItemConstants.INCO_TERM_LABEL,
                    lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL
            ])
            .setHiddenColumns(["typedId"])
            .setReadOnlyColumns([
                    lineItemConstants.MATERIAL_LABEL,
                    lineItemConstants.SHIP_TO_LABEL,
                    lineItemConstants.PLANT_LABEL
            ])
            .setColumnValueOptions([
                    (lineItemConstants.INCO_TERM_LABEL)                 : incoTermOptions,
                    (lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL)   : meansOfTransportationOptions
            ])
            .setValue(matrixValues)
            .buildMap()

    customFormProcessor.addOrUpdateInput(param)
}

return null

def findOutput (item, resultName) {
    return item.outputs.find { it.resultName == resultName }?.result ?: ""
}