if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry("DeliveredPriceInputPrevious")
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input["DeliveredPriceInputPrevious"]
}