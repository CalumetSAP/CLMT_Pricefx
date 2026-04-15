if (api.isInputGenerationExecution()) {
    return api.inputBuilderFactory()
            .createConfiguratorInputBuilder("Inputs", "PricingFormulaLineItemConfiguratorPL", true)
            .getInput()
} else {
    return input.Inputs
}
