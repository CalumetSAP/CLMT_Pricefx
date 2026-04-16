if (api.isInputGenerationExecution()) {
    api.inputBuilderFactory().createHiddenEntry("AdderInputPrevious")
            .setRequired(false)
            .setReadOnly(true)
            .getInput()
} else {
    return input["AdderInputPrevious"]
}