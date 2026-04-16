if (api.isInputGenerationExecution()) {
    return api.inputBuilderFactory()
            .createConfiguratorInputBuilder("Inputs", "RailFreightPriceMaintenanceConfigurator", true)
            .getInput()
} else {
    return input.Inputs
}