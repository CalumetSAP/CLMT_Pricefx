if (customFormProcessor.isPostPhase()) return

Map<String, Object> finalizeQuote = customFormProcessor.getHelper().getRoot().getInputByName("FinalizeQuoteInput")

if (!finalizeQuote) {
    def param = api.inputBuilderFactory()
            .createBooleanUserEntry("FinalizeQuoteInput")
            .setLabel("Finalize quote (only if quote is not in 'Draft' status)")
            .setNoRefresh(true)
            .buildMap()
    customFormProcessor.addOrUpdateInput(param)
}

return null