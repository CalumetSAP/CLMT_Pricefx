/**
 * Retrieves the advanced configuration stored under the provided configurationName.
 * In case an already existing configuration provided by currentConfiguration the fetched configuration is appended to it.
 * @param currentConfiguration current advanced configuration that will have the fetched configuration appended
 * @param configurationName name for the advanced configuration that should be fetched from the environment.
 * @return Map of the advanced configuration
 */
Map getAdvancedConfiguration(Map currentConfiguration, String configurationName) {
    String advancedConfigurationString = getAdvancedConfigurationString(configurationName)

    return advancedConfigurationString ? appendAdvancedConfiguration(advancedConfigurationString, currentConfiguration) : currentConfiguration
}

/**
 * Appends the fetched advanced configuration to a given currentConfiguration.
 * Because the fetched advanced configuration is in String format it needs to be decoded to Map.
 * @param advancedConfigurationString String containing the definition of the fetched advanced configuration
 * @param currentConfiguration current advanced configuration that will have the fetched configuration appended
 * @return Map of the current configuration with newly fetched advanced configuration
 */
protected Map appendAdvancedConfiguration(String advancedConfigurationString, Map currentConfiguration) {
    def advancedConfigurationObj = api.jsonDecode(advancedConfigurationString)
    currentConfiguration << advancedConfigurationObj

    return currentConfiguration
}

/**
 * Fetches the advanced configuration String from the environment.
 * @param configurationName name for the advanced configuration that should be fetched
 * @return Sting value of the advanced configuration
 */
protected String getAdvancedConfigurationString(String configurationName) {
    def advancedConfiguration = api.find("AP", Filter.equal("uniqueName", configurationName))

    return advancedConfiguration?.getAt(0)?.value
}

/**
 * Retrieves the advanced configuration used by the SIP_Dashboards_Commons library
 * @return advanced configuration of the SIP_Dashboards_Commons
 */
Map getCommonsAdvancedConfiguration() {
    String configurationName = libs.SIP_Dashboards_Commons.ConstConfig.COMMONS_ADVANCED_CONFIGURATION_NAME

    return libs.SharedLib.CacheUtils.getOrSet(configurationName, [configurationName], { String _configurationName ->
        return getAdvancedConfiguration([:], _configurationName)
    })
}
