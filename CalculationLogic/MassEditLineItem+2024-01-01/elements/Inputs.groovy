if (api.isInputGenerationExecution()) {
    return api.inputBuilderFactory()
            .createConfiguratorInputBuilder("Inputs", "MassEditConfigurator", true)
            .getInput()
} else {
    return input.Inputs
}
