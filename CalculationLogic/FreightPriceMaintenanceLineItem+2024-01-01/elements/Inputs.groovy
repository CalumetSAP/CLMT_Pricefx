if (api.isInputGenerationExecution()) {
    return api.inputBuilderFactory()
            .createConfiguratorInputBuilder("Inputs", "FreightPriceMaintenanceConfigurator", true)
            .getInput()
} else {
    return input.Inputs
}