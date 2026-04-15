if (api.isInputGenerationExecution()) {
    return api.inputBuilderFactory()
            .createConfiguratorInputBuilder("Inputs", "PricelistZBPLConfigurator", true)
            .getInput()
} else {
    return api.getParameter("Inputs")
}
