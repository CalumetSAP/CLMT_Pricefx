if (!customFormProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def customFormProcessorRoot = customFormProcessor.getHelper().getRoot()
api.logInfo("Suganya Testing here")
api.logInfo("customFormProcessorRoot")
String quoteTypedId = api.currentItem().parentTypedId
api.logInfo("Suganya Testing here")
api.logInfo("quoteTypedId")
def lineItems = api.getCalculableLineItemCollection(quoteTypedId)?.lineItems
api.logInfo("Suganya Testing here")
api.logInfo("lineItems")
def groupedMatrixValues = customFormProcessorRoot.getInputByName("FinalizeQuoteMatrix")?.get("value")?.groupBy { it.typedId } ?: [:]

def lineItemsToUpdate = []
Boolean updatedItem = false
def configuratorValue, matrixValue, selectedIncoTerm, selectedMeansOfTransportation
for (lineItem in lineItems) {
    configuratorValue = lineItem.inputs?.find { it.name == lineItemConstants.CONFIGURATOR_NAME }?.value
    matrixValue = groupedMatrixValues?.get(lineItem.typedId)?.find()

    selectedIncoTerm = matrixValue?.get(lineItemConstants.INCO_TERM_LABEL)?.split(" - ")?.find()
    if (configuratorValue?.get(lineItemConstants.INCO_TERM_ID) != selectedIncoTerm) {
        lineItem.inputs?.find { it.name == lineItemConstants.CONFIGURATOR_NAME }?.value[lineItemConstants.INCO_TERM_ID] = selectedIncoTerm
        updatedItem = true
    }

    selectedMeansOfTransportation = matrixValue?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL)?.split(" - ")?.find()
    if (configuratorValue?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_ID) != selectedMeansOfTransportation) {
        lineItem.inputs?.find { it.name == lineItemConstants.CONFIGURATOR_NAME }?.value[lineItemConstants.MEANS_OF_TRANSPORTATION_ID] = selectedMeansOfTransportation
        updatedItem = true
    }

    if (updatedItem) {
        lineItemsToUpdate.add([
                inputs  : lineItem.inputs?.collect { [name: it.name, value: it.value] },
                typedId : lineItem.typedId,
                version : lineItem.version
        ])
        updatedItem = false
    }
}

//Update line items that have different "Means of Transportation"
def body, response
for (lineItem in lineItemsToUpdate) {
    body = [
            "data": [
                    "lineItems": [lineItem]
            ]
    ]
    api.logInfo("Suganya Testing here " )
    response = api.boundCall("SystemUpdate", "/clicmanager.updatelineitems/${quoteTypedId}", api.jsonEncode(body).toString(), false)
    api.logInfo("Line item updated for quote ${quoteTypedId}. Response", response)
}