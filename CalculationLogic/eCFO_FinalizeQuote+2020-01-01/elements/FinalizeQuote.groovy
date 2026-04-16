if (!customFormProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def customFormProcessorRoot = customFormProcessor.getHelper().getRoot()
String quoteStatus = api.find("Q", 0, 1, null, Filter.equal("typedId", api.currentItem().parentTypedId))?.find()?.quoteStatus
Boolean isDraft = "draft".equalsIgnoreCase(quoteStatus)
Map<String, Object> finalizeQuote = customFormProcessorRoot.getInputByName("FinalizeQuoteInput")

if (isDraft) {
    Map<String, Object> finalizeQuoteMatrix = customFormProcessorRoot.getInputByName("FinalizeQuoteMatrix")

    finalizeQuoteMatrix.parameterConfig.readOnlyColumns = [
            lineItemConstants.MATERIAL_LABEL,
            lineItemConstants.SHIP_TO_LABEL,
            lineItemConstants.PLANT_LABEL
    ]
    finalizeQuote?.value = false
    finalizeQuote?.readOnly = false

    customFormProcessor.addOrUpdateInput(finalizeQuoteMatrix)
    customFormProcessor.addOrUpdateInput(finalizeQuote)
} else if (finalizeQuote?.value) {
    Map<String, Object> finalizeQuoteMatrix = customFormProcessorRoot.getInputByName("FinalizeQuoteMatrix")

    finalizeQuoteMatrix.parameterConfig.readOnlyColumns.addAll([lineItemConstants.INCO_TERM_LABEL, lineItemConstants.MEANS_OF_TRANSPORTATION_LABEL])
    finalizeQuote.readOnly = true

    customFormProcessor.addOrUpdateInput(finalizeQuoteMatrix)
    customFormProcessor.addOrUpdateInput(finalizeQuote)
}