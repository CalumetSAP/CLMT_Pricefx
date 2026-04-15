import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry

/**
 * Adds the calculation model parameter used in the Outliers Dashboards to the given configurator.
 * The configuration for the input is stored in libs.SIP_Dashboards_Commons.ConstConfig.OUTLIERS_DASHBOARD_CONFIG.INPUTS.MODELS
 * @param configuratorEntry the configurator entry to which the parameter will be appended
 * @return parameter defining the calculation model input
 */
def addModelSelectionParameter(ConfiguratorEntry configuratorEntry) {
    Map inputModelsConfig = libs.SIP_Dashboards_Commons.ConstConfig.OUTLIERS_DASHBOARD_CONFIG.INPUTS.MODELS

    def modelsParameter = configuratorEntry.createParameter(InputType.OPTION, inputModelsConfig.UNIQUE_KEY)
            .setLabel(inputModelsConfig.LABEL)
            .setValueOptions(inputModelsConfig.VALUES.keySet() as List)

    modelsParameter.addParameterConfigEntry("labels", inputModelsConfig.VALUES)

    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(modelsParameter, inputModelsConfig.DEFAULT_VALUE)

    return modelsParameter
}

/**
 * Adds the KPI selection parameter used in the Outliers Dashboards to the given configurator.
 * The available KPI's are dependant on the selected model.
 * The configuration for the input is stored in libs.SIP_Dashboards_Commons.ConstConfig.OUTLIERS_DASHBOARD_CONFIG.INPUTS.KPI
 * @param configuratorEntry the configurator entry to which the parameter will be appended
 * @param selectedModel selected calculation model, the list of available models can be found in the SIP_Dashboards_Commons.ConstConfig
 * @return parameter defining the KPI selection input
 */
def addKPISelectionParameter(ConfiguratorEntry configuratorEntry, String selectedModel) {
    Map dashboardConfig = libs.SIP_Dashboards_Commons.ConstConfig.OUTLIERS_DASHBOARD_CONFIG
    Map inputKPIConfig = dashboardConfig.INPUTS.KPI

    Map modelKpiConfig = inputKPIConfig.MODELS.getAt(selectedModel)
    Map modelLabelConfig = modelKpiConfig.VALUES.collectEntries { [(it): dashboardConfig.FIELDS.getAt(it).LABEL] }

    def kpiParameter = configuratorEntry.createParameter(InputType.OPTION, inputKPIConfig.UNIQUE_KEY)
            .setLabel(inputKPIConfig.LABEL)
            .setValueOptions(modelLabelConfig.keySet() as List)
    kpiParameter.addParameterConfigEntry("labels", modelLabelConfig)

    libs.SIP_Dashboards_Commons.ConfiguratorUtils.assertCurrentConfiguratorValue(kpiParameter, modelKpiConfig.VALUES)
    libs.SIP_Dashboards_Commons.ConfiguratorUtils.setParameterDefaultValue(kpiParameter, modelKpiConfig.DEFAULT_VALUE)

    return kpiParameter
}