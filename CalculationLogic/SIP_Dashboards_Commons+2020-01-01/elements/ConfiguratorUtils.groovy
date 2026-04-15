import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry
import net.pricefx.server.dto.calculation.ContextParameter

/**
 * Get input value of the configurator entry
 * @param entry ConfiguratorEntry the entry
 * @return the value of configurator entry
 */
def getInputValue(ConfiguratorEntry entry) {
    return entry?.getFirstInput()?.value
}

/**
 * Create multi-tier configurator entry
 * @param name
 * @param label
 * @param valueHint
 * @param inputMessage
 * @param isRequired
 * @return configurator entry
 */
ConfiguratorEntry createMultiTierConfiguratorEntry(
        String name,
        String label,
        String valueHint,
        String inputMessage = null,
        Boolean isRequired = true) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    ContextParameter contextParameter = createContextParameter(configuratorEntry, InputType.MULTITIERENTRY, name, label, isRequired)

    contextParameter.setLabel(label)
    contextParameter.setValueHint(valueHint)

    if (inputMessage) {
        configuratorEntry.setMessage(inputMessage)
    }

    return configuratorEntry
}

/**
 * Create user entry configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @param noRefresh
 * @return configurator entry
 */
ConfiguratorEntry createUserEntryConfiguratorEntry(
        String name,
        String label,
        Boolean isRequired = true,
        Boolean noRefresh = true,
        String defaultValue = null,
        Boolean isReadOnly = false) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.USERENTRY, name, label, isRequired, defaultValue, isReadOnly, noRefresh)

    return configuratorEntry
}

/**
 * Create boolean configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @param noRefresh
 * @param defaultValue
 * @param isReadOnly
 * @return configurator entry
 */
ConfiguratorEntry createBooleanEntryConfiguratorEntry(
        String name,
        String label,
        Boolean isRequired = true,
        Boolean noRefresh = true,
        String defaultValue = null,
        Boolean isReadOnly = false) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.BOOLEANUSERENTRY, name, label, isRequired, defaultValue, isReadOnly, noRefresh)

    return configuratorEntry
}

/**
 * Create string configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @param noRefresh
 * @param defaultValue
 * @param isReadOnly
 * @return configurator entry
 */
ConfiguratorEntry createStringEntryConfiguratorEntry(
        String name,
        String label,
        Boolean isRequired = true,
        Boolean noRefresh = true,
        String defaultValue = null,
        Boolean isReadOnly = false) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.STRINGUSERENTRY, name, label, isRequired, defaultValue, isReadOnly, noRefresh)

    return configuratorEntry
}

/**
 * Create integer configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @param defaultValue
 * @param isReadOnly
 * @param lowerBoundary
 * @param upperBoundary
 * @return configurator entry
 */
ConfiguratorEntry createIntegerEntryConfiguratorEntry(
        String name,
        String label,
        Boolean isRequired = true,
        Integer defaultValue = null,
        Boolean isReadOnly = false,
        Boolean noRefresh = true) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.INTEGERUSERENTRY, name, label, isRequired, defaultValue, isReadOnly, noRefresh)

    return configuratorEntry
}

/**
 * Create date configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @param defaultValue
 * @param isReadOnly
 * @param lowerBoundary
 * @param upperBoundary
 * @return configurator entry
 */
ConfiguratorEntry createDateEntryConfiguratorEntry(
        String name,
        String label,
        Boolean isRequired = true,
        Date defaultValue = null,
        Boolean isReadOnly = false,
        Boolean noRefresh = true) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.DATEUSERENTRY, name, label, isRequired, defaultValue, isReadOnly, noRefresh)

    return configuratorEntry
}

/**
 * Create product group configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @return configurator entry
 */
ConfiguratorEntry createProductGroupEntryConfiguratorEntry(String name, String label, Boolean isRequired = true) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.PRODUCTGROUP, name, label, isRequired)

    return configuratorEntry
}

/**
 * Create customer group configurator entry
 * @param name
 * @param label
 * @param isRequired
 * @return configurator entry
 */
ConfiguratorEntry createCustomerGroupEntryConfiguratorEntry(String name, String label, Boolean isRequired = true) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    createContextParameter(configuratorEntry, InputType.CUSTOMERGROUP, name, label, isRequired)

    return configuratorEntry
}

/**
 * Create hidden configurator entry
 * @param name
 * @param label
 * @param setValue
 * @param valueIsDefaultOnly
 * @return configurator entry
 */
ConfiguratorEntry createHiddenConfiguratorEntry(
        String name,
        String label,
        Object setValue,
        Boolean valueIsDefaultOnly = false) {
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()
    ContextParameter contextParameter = createContextParameter(configuratorEntry, InputType.HIDDEN, name, label, false)

    if (!valueIsDefaultOnly || contextParameter.getValue() != setValue || contextParameter.getValue() == null) {
        contextParameter.setValue(setValue)
    }

    return configuratorEntry
}

/**
 * Create option configurator entry
 * @param name
 * @param label
 * @param options
 * @param defaultValue
 * @param isRequired
 * @param multiSelect
 * @param defaultValueWhenSingleChoice
 * @param noRefresh
 * @param labelsForOptions
 * @param isReadOnly
 * @return configurator entry
 */
ConfiguratorEntry createOptionConfiguratorEntry(
        String name,
        String label,
        List options,
        Object defaultValue = null,
        Boolean isRequired = true,
        Boolean multiSelect = false,
        Boolean defaultValueWhenSingleChoice = true,
        Boolean noRefresh = false,
        Map labelsForOptions = null,
        Boolean isReadOnly = false) {
    InputType inputType = multiSelect ? InputType.OPTIONS : InputType.OPTION
    ConfiguratorEntry configuratorEntry = api.createConfiguratorEntry()

    createOptionContextParameter(configuratorEntry, inputType, name, label, isRequired, options, defaultValue, defaultValueWhenSingleChoice, noRefresh, labelsForOptions, isReadOnly)

    return configuratorEntry
}

/**
 * Create currency option configurator entry
 * @param name
 * @param label
 * @param datamartName
 * @param isRequired
 * @param multiSelect
 * @param defaultValueWhenSingleChoice
 * @param noRefresh
 * @param labelsForOptions
 * @param isReadOnly
 * @return configurator entry
 */
ConfiguratorEntry getCurrencyOptionConfiguratorEntry(String name,
                                                     String label,
                                                     String datamartName,
                                                     Boolean isRequired = true,
                                                     Boolean multiSelect = false,
                                                     Boolean defaultValueWhenSingleChoice = true,
                                                     Boolean noRefresh = false,
                                                     Map labelsForOptions = null,
                                                     Boolean isReadOnly = false) {

    List<String> availableCurrencies = libs.SIP_Dashboards_Commons.CurrencyUtils.getAvailableCurrencies(datamartName) ?: []
    String defaultValue = null

    if (availableCurrencies.size() > 1) {
        defaultValue = libs.SIP_Dashboards_Commons.CurrencyUtils.getDatamartCurrencyCode(datamartName)
    } else if (availableCurrencies.size() == 1) {
        defaultValue = availableCurrencies.get(0)
    }

    return createOptionConfiguratorEntry(name, label, availableCurrencies, defaultValue, isRequired, multiSelect, defaultValueWhenSingleChoice, noRefresh, labelsForOptions, isReadOnly)
}

/**
 * Create context parameter for option configurator entry
 * @param configuratorEntry
 * @param type
 * @param name
 * @param label
 * @param isRequired
 * @param options
 * @param defaultValue
 * @param defaultValueWhenSingleChoice
 * @param noRefresh
 * @param labelsForOptions
 * @param isReadOnly
 * @return context parameter
 */
ContextParameter createOptionContextParameter(
        ConfiguratorEntry configuratorEntry,
        InputType type,
        String name,
        String label,
        Boolean isRequired = true,
        List options,
        Object defaultValue = null,
        Boolean defaultValueWhenSingleChoice = true,
        Boolean noRefresh = false,
        Map labelsForOptions = null,
        Boolean isReadOnly = false) {
    ContextParameter contextParameter = createContextParameter(configuratorEntry, type, name, label, isRequired, defaultValue, isReadOnly, noRefresh)
    contextParameter.setValueOptions(options.collect { it.toString() })

    if (labelsForOptions) {
        contextParameter.addParameterConfigEntry("labels", labelsForOptions)
    }

    if (type == InputType.OPTION) {
        String currentValue = contextParameter?.getValue()

        if (!options?.contains(currentValue)) {
            contextParameter.setValue(null)
        }
    }

    if (contextParameter?.getValue() == null && options?.size() == 1 && defaultValueWhenSingleChoice) {
        contextParameter.setValue(options.get(0))
    }

    return contextParameter
}

/**
 * Create context parameter for a configurator entry
 * @param configuratorEntry
 * @param type
 * @param name
 * @param label
 * @param isRequired
 * @param defaultValue
 * @param isReadOnly
 * @return context parameter
 */
ContextParameter createContextParameter(
        ConfiguratorEntry configuratorEntry,
        InputType type,
        String name,
        String label,
        Boolean isRequired = true,
        Object defaultValue = null,
        Boolean isReadOnly = false,
        Boolean noRefresh = false) {
    ContextParameter contextParameter = configuratorEntry.createParameter(type, name)
    contextParameter.setLabel(label)

    if (isRequired) {
        contextParameter.setRequired(true)
    }

    if (contextParameter?.getValue() == null && defaultValue != null) {
        contextParameter.setValue(defaultValue)
    }

    if (isReadOnly) {
        contextParameter.readOnly = isReadOnly
    }

    if (noRefresh) {
        contextParameter.addParameterConfigEntry("noRefresh", true)
    }

    return contextParameter
}

/**
 * Sets the default value or user default value for a given configurator.
 * Only configurators with one input entry are supported by this method.
 * @param configuratorEntry configurator entry that contains one input parameter to be used for the default value
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 */
void setConfiguratorEntryDefaultValue(def configuratorEntry, def defaultValue) {
    def firstInput = configuratorEntry?.getFirstInput()

    if (configuratorEntry != null && firstInput) {
        setParameterDefaultValue(firstInput, defaultValue)
    }
}

/**
 * Sets the default value for a given ContextParameter.
 * The value set depends on data availability.
 * In case both default value and userDefaultValue are present the userDefaultValue always has priority.
 * @param parameter parameter for which the default value should be set
 * @param userDefaultValue value defined by the user for the given entry in the Default Filters configurator wizard.
 */
void setParameterDefaultValue(def parameter, def defaultValue) {
    if (parameter != null && parameter.getValue() == null) {
        parameter.setValue(defaultValue)
    }
}

/**
 * Validates whether the given configurator parameter value is in one of the values that are allowed,
 * if not the value is set to null.
 * Used to reset the configurator parameters in various configurators.
 * @param configuratorParameter parameter for which the check should be made
 * @param allowedValues list of all values that the provided configurator parameter can have
 */
void assertCurrentConfiguratorValue(def configuratorParameter, List allowedValues) {
    def storedConfiguratorValue = configuratorParameter?.getValue()

    if (storedConfiguratorValue != null && !allowedValues.contains(storedConfiguratorValue)) {
        configuratorParameter.setValue(null)
    }
}

/**
 * Sets the noRefresh flag to true on a given configurator entry
 * @param configuratorEntry configurator entry to set the noRefresh flag for
 */
void setNoRefreshParameter(ConfiguratorEntry configuratorEntry) {
    configuratorEntry?.getFirstInput()?.addParameterConfigEntry("noRefresh", true)
}

/**
 * Defines the values and labels values based on a strict structure of the const config provided.
 * The constConfig provided must be of such structure:
 * [(WATERFALL_MODEL_ABSOLUTE_NAME)      : [LABEL: "Absolute"],
 *  (WATERFALL_MODEL_DETAIL_NAME)        : [LABEL: "Absolute Detail"],
 *  (WATERFALL_MODEL_ABSOLUTE_UNIT_NAME) : [LABEL: "By Absolute Unit"],
 *  (WATERFALL_MODEL_PERCENTAGE_NAME)    : [LABEL: "Percentage]]
 * The key will be used as a key returned from the entry, the LABEL will be used for mapping between key-display label.
 * @param configuratorConstConfig constConfig of a given configurator retrieved from libs.SIP_Dashboards_Commons.ConstConfig element
 * @return a Map of values and labels to be used in an option input
 */
Map getDashboardInputOptionKeyLabelConfig(Map configuratorConstConfig) {
    return [values: configuratorConstConfig.keySet() as List,
            labels: configuratorConstConfig.collectEntries { [(it.key): it.value.LABEL] }]
}