if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry("PriceInputPrevious")
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input["PriceInputPrevious"]
}