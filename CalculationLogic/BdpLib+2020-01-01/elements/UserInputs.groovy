import net.pricefx.common.api.FieldFormatType
import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ConfiguratorEntry
import net.pricefx.server.dto.calculation.ContextParameter

/**
 * Adds a callback configuration to an input element that triggers on change of its value.
 *
 * @param entry The ConfiguratorEntry object containing the input element to add the callback configuration to.
 * @param onChangeCallback The callback function/clojure to execute when the value of the input element changes.
 * @return The ConfiguratorEntry object with the callback configuration added to the input element.
 */

ConfiguratorEntry addCallbackConfigurationOnChange(ConfiguratorEntry entry, Closure onChangeCallback) {
	def input = entry.getFirstInput()
	def newValue = input.getValue()

	// keep track on changes
	ContextParameter previousValue = entry.createParameter(InputType.HIDDEN, input.getName() + "Previous")

	if (previousValue.getValue() == null) {
		previousValue.setValue(newValue)
	} else {
		if (newValue != previousValue.getValue()) {
			previousValue.setValue(newValue)
			onChangeCallback(newValue)
		}
	}

	return entry
}

ConfiguratorEntry addCallbackConfigurationOnChangeWithNull(ConfiguratorEntry entry, Closure onChangeCallback) {
	def input = entry.getFirstInput()
	def newValue = input.getValue()

	// keep track on changes
	ContextParameter previousValue = entry.createParameter(InputType.HIDDEN, input.getName() + "Previous")

	if (newValue != previousValue.getValue()) {
		previousValue.setValue(newValue)
		onChangeCallback(newValue)
	}

	return entry
}

/**
 Adds a default value to a ConfiguratorEntry if the existing value is null.
 If the ConfiguratorEntry is read-only and the existing value is not the same as the default value, the value is replaced with the default value.
 @param entry The ConfiguratorEntry to add a default value to
 @param defaultValue The default value to use if the existing value is null
 @return The ConfiguratorEntry with the default value added or updated, if necessary
 */
ConfiguratorEntry addDefaultValueOnNull(ConfiguratorEntry entry, Object defaultValue) {
	def parameter = entry.getFirstInput()
	def isReadOnly = parameter.getReadOnly()
	def value = parameter?.getValue()
	if (parameter && !value) {
		parameter.setValue(defaultValue)
	}
	if (isReadOnly && value && value != defaultValue)
		parameter.setValue(defaultValue)
	return entry
}

/**
 Adds a default value to a ConfiguratorEntry if the existing value is null.
 If the ConfiguratorEntry is read-only do nothing.
 @param entry The ConfiguratorEntry to add a default value to
 @param defaultValue The default value to use if the existing value is null
 @return The ConfiguratorEntry with the default value added or updated, if necessary
 */
ConfiguratorEntry addDefaultValueOnNullWithoutReadOnly(ConfiguratorEntry entry, Object defaultValue) {
	def parameter = entry.getFirstInput()
	def value = parameter?.getValue()
	if (parameter && !value) {
		parameter.setValue(defaultValue)
	}
	return entry
}

/**
 * @param ConfiguratorEntry entry
 * @param InputType type
 * @param boolean isHide
 * @return When isHide is true, set the configurator type to hidden.
 */
ConfiguratorEntry addIsHide(ConfiguratorEntry entry, InputType type, boolean isHide) {
	def switchType = isHide ? InputType.HIDDEN : type
	if (isHide) {
		entry.getFirstInput().setValue(null)
	}
	entry.getFirstInput().applyType(switchType)
	return entry
}


/**
 *
 * @param list
 * @param validateColumns
 * @return
 */
def filterMapByFieldsList(list, validateColumns) {
	return list.collect { row -> row.findAll { validateColumns.contains(it.getKey()) } }
}

/**
 *
 * @param type
 * @return String format of FieldFormatType Enum
 */
def getCode(FieldFormatType type) {
	def c = type.toString().toLowerCase().toList()
	c[0] = c[0].toUpperCase()
	c.join()
}

/**
 * Creates an input element with the specified properties.
 *
 * @param id The ID of the input element.
 * @param type The type of the input element.
 * @param label The label of the input element.
 * @param isRequired A boolean value indicating whether the input element is required or not.
 * @param isReadOnly A boolean value indicating whether the input element is read-only or not.
 * @return A ConfiguratorEntry object containing the input element with the specified properties.
 */
ConfiguratorEntry createInput(String id, InputType type, String label, boolean isRequired, boolean isReadOnly) {
	ConfiguratorEntry entry = api.createConfiguratorEntry(type, id)
	ContextParameter parameter = entry.getFirstInput()

	parameter.setLabel(label)
	parameter.setRequired(isRequired)
	parameter.setReadOnly(isReadOnly)

	return entry
}

/**
 * Creates an input element with the specified properties and an onChange callback configuration.
 *
 * @param id The ID of the input element.
 * @param type The type of the input element.
 * @param label The label of the input element.
 * @param isRequired A boolean value indicating whether the input element is required or not.
 * @param isReadOnly A boolean value indicating whether the input element is read-only or not.
 * @param onChangeCallback The callback function to execute when the value of the input element changes.
 * @return A ConfiguratorEntry object containing the input element with the specified properties and callback configuration.
 */
@Deprecated
ConfiguratorEntry createInput(String id, InputType type, String label, boolean isRequired, boolean isReadOnly, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInput(id, type, label, isRequired, isReadOnly),
			onChangeCallback
	)
}

/**
 * Creates a ConfiguratorEntry object with an input of type string user entry.
 *
 * @param id The identifier for the input string user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @return A ConfiguratorEntry object with a string user entry input
 */
ConfiguratorEntry createInputString(String id, String label, boolean isRequired, boolean isReadOnly) {
	return createInput(id, InputType.STRINGUSERENTRY, label, isRequired, isReadOnly)
}

/**
 * Creates a ConfiguratorEntry object with an input of type string user entry, and sets a default value if the existing value is null.
 *
 * @param id The identifier for the input string user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param defaultValue The default value to use if the existing value is null
 * @return A ConfiguratorEntry object with a string user entry input and a default value, if necessary
 */
ConfiguratorEntry createInputString(String id, String label, boolean isRequired, boolean isReadOnly, String defaultValue) {
	return addDefaultValueOnNull(
			createInputString(id, label, isRequired, isReadOnly),
			defaultValue
	)
}

/**
 * Creates a ConfiguratorEntry object with an input of type string user entry, and sets a default value if the existing value is null.
 *
 * @param id The identifier for the input string user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param defaultValue The default value to use if the existing value is null
 * @param defaultWhenReadOnly Default the value when it´s read only
 * @return A ConfiguratorEntry object with a string user entry input and a default value, if necessary
 */
ConfiguratorEntry createInputString(String id, String label, boolean isRequired, boolean isReadOnly, String defaultValue, boolean defaultWhenReadOnly) {
	if (defaultWhenReadOnly) {
		return addDefaultValueOnNull(
				createInputString(id, label, isRequired, isReadOnly),
				defaultValue
		)
	} else {
		return addDefaultValueOnNullWithoutReadOnly(
				createInputString(id, label, isRequired, isReadOnly),
				defaultValue
		)
	}
}

/**
 * Creates a ConfiguratorEntry object with an input of type string user entry, and adds a callback function to be executed when the input value changes.
 *
 * @param id The identifier for the input string user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param onChangeCallback The callback function to be executed when the input value changes
 * @return A ConfiguratorEntry object with a string user entry input and a callback function to be executed when the input value changes
 */
ConfiguratorEntry createInputString(String id, String label, boolean isRequired, boolean isReadOnly, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputString(id, label, isRequired, isReadOnly),
			onChangeCallback
	)
}

/**
 * Creates a ConfiguratorEntry object with an input of type string user entry, and adds a default value if the existing value is null. Additionally, a callback function is added to be executed when the input value changes.
 *
 * @param id The identifier for the input string user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param defaultValue The default value to use if the existing value is null
 * @param onChangeCallback The callback function to be executed when the input value changes
 * @return A ConfiguratorEntry object with a string user entry input, a default value if necessary, and a callback function to be executed when the input value changes
 */
ConfiguratorEntry createInputString(String id, String label, boolean isRequired, boolean isReadOnly, String defaultValue, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputString(id, label, isRequired, isReadOnly, defaultValue),
			onChangeCallback
	)
}

///**
// * Creates a configuration entry for a string input type with additional parameters such as
// * minimum and maximum lengths.
// *
// * @param id The unique identifier for the input string configuration entry
// * @param label The label to be displayed for the input string configuration entry
// * @param isRequired A boolean flag indicating whether the input string is required or not
// * @param isReadOnly A boolean flag indicating whether the input string is read-only or not
// * @param minLength An integer representing the minimum length of the input string, or null if not applicable
// * @param maxLength An integer representing the maximum length of the input string, or null if not applicable
// * @param defaultValue The default value for the input string configuration entry
// * @return The created ConfiguratorEntry instance for the input string configuration
// */
//ConfiguratorEntry createInputString(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, Integer minLength, Integer maxLength) {
//	def entry = createInputString(id, label, isRequired, isReadOnly, defaultValue)
//	if (minLength != null)
//		entry.getFirstInput().addParameterConfigEntry("minLength", minLength)
//	if (maxLength != null)
//		entry.getFirstInput().addParameterConfigEntry("maxLength", maxLength)
//	return entry
//}

/**
 * Creates a ConfiguratorEntry object with an input of type integer user entry.
 *
 * @param id The identifier for the input integer user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @return A ConfiguratorEntry object with a integer user entry input.
 */
ConfiguratorEntry createInputNumber(String id, String label, boolean isRequired, boolean isReadOnly) {
	def entry = createInput(id, InputType.INTEGERUSERENTRY, label, isRequired, isReadOnly)
	return entry
}

/**
 * Creates a ConfiguratorEntry object with an input of type integer user entry, and adds a default value if the existing value is null.
 *
 * @param id The identifier for the input integer user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param defaultValue The default value to use if the existing value is null
 * @return A ConfiguratorEntry object with a integer user entry input and a default value, if necessary
 */
ConfiguratorEntry createInputNumber(String id, String label, boolean isRequired, boolean isReadOnly, Integer defaultValue) {
	return addDefaultValueOnNull(
			createInputNumber(id, label, isRequired, isReadOnly),
			defaultValue
	)
}

/**
 * Creates a ConfiguratorEntry object with an input of type integer user entry, additionally, a callback function is added to be executed when the input value changes.
 *
 * @param id The identifier for the input integer user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param onChangeCallback The callback function to be executed when the input value changes
 * @return A ConfiguratorEntry object with a integer user entry input, and a callback function to be executed when the input value changes
 */
ConfiguratorEntry createInputNumber(String id, String label, boolean isRequired, boolean isReadOnly, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputNumber(id, label, isRequired, isReadOnly),
			onChangeCallback
	)
}

/**
 * Creates a ConfiguratorEntry object with an input of type integer user entry, and adds a default value if the existing value is null. Additionally, a callback function is added to be executed when the input value changes.
 *
 * @param id The identifier for the input integer user entry
 * @param label The label to be displayed next to the input
 * @param isRequired Indicates whether the input is required to be filled out by the user
 * @param isReadOnly Indicates whether the input is read-only and cannot be edited by the user
 * @param defaultValue The default value to use if the existing value is null
 * @param onChangeCallback The callback function to be executed when the input value changes
 * @return A ConfiguratorEntry object with a integer user entry input, a default value if necessary, and a callback function to be executed when the input value changes
 */
ConfiguratorEntry createInputNumber(String id, String label, boolean isRequired, boolean isReadOnly, Integer defaultValue, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputNumber(id, label, isRequired, isReadOnly, defaultValue),
			onChangeCallback
	)
}

/**
 * Creates a ConfiguratorEntry object with an input of type decimal user entry.
 *
 * @param id The identifier for the decimal user entry input
 * @param label The label to be displayed next to the input field
 * @param isRequired Indicates whether the input field is required to be filled by the user
 * @param isReadOnly Indicates whether the input field is read-only and cannot be modified by the user
 * @return A ConfiguratorEntry object with a decimal user entry input
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly) {
	def entry = createInput(id, InputType.USERENTRY, label, isRequired, isReadOnly)
	entry.getFirstInput().setConfigParameter("dataType", "float");

	return entry
}

/**
 * Creates a ConfiguratorEntry for a decimal input field.
 *
 * @param id The unique identifier for the input field.
 * @param label The label to display next to the input field.
 * @param isRequired Whether the input field is required or optional.
 * @param isReadOnly Whether the input field is read-only or editable.
 * @param formatType The format type for the decimal input field.Possible values are "Percent" and "###.000".
 * @return A ConfiguratorEntry object representing the created decimal input field.
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, String formatType) {
	def entry = createInputDecimal(id, label, isRequired, isReadOnly)
	entry.getFirstInput().setConfigParameter("formatType", formatType);

	return entry
}

/**
 * Creates a ConfiguratorEntry for a decimal input field.
 *
 * @param id The unique identifier for the input field.
 * @param label The label to display next to the input field.
 * @param isRequired Whether the input field is required or optional.
 * @param isReadOnly Whether the input field is read-only or editable.
 * @param formatType The format type for the decimal input field.Possible values are "Percent" and "###.000".
 * @return A ConfiguratorEntry object representing the created decimal input field.
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, String formatType, boolean isHide) {
	def entry = createInputDecimal(id, label, isRequired, isReadOnly)
	entry.getFirstInput().setConfigParameter("formatType", formatType);

	return addIsHide(entry, InputType.USERENTRY, isHide)
}

/**
 * Creates a ConfiguratorEntry object with a decimal user entry input and a default value.
 *
 * If the input value is null, the default value is used.
 * @param id The identifier for the decimal user entry input
 * @param label The label to be displayed next to the input field
 * @param isRequired Indicates whether the input field is required to be filled by the user
 * @param isReadOnly Indicates whether the input field is read-only and cannot be modified by the user
 * @param defaultValue The default value to be used if the input value is null
 * @return A ConfiguratorEntry object with a decimal user entry input and a default value
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, BigDecimal defaultValue) {
	return addDefaultValueOnNull(
			createInputDecimal(id, label, isRequired, isReadOnly),
			defaultValue
	)
}

/**
 * Creates a ConfiguratorEntry object with a decimal user entry input and a default value.
 *
 * If the input value is null, the default value is used.
 * @param id The identifier for the decimal user entry input
 * @param label The label to be displayed next to the input field
 * @param isRequired Indicates whether the input field is required to be filled by the user
 * @param isReadOnly Indicates whether the input field is read-only and cannot be modified by the user
 * @param defaultValue The default value to be used if the input value is null
 * @param formatType The format type for the decimal input field. Possible values are "Percent" and "###.000".
 * @return A ConfiguratorEntry object with a decimal user entry input and a default value
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, BigDecimal defaultValue, String formatType) {
	return addDefaultValueOnNull(
			createInputDecimal(id, label, isRequired, isReadOnly, formatType),
			defaultValue
	)
}

/**
 * Creates a `ConfiguratorEntry` object for a decimal user input field with an `onChange` callback.
 *
 * @param id The unique identifier for the input field.
 * @param label The label or prompt for the input field.
 * @param isRequired Whether the input field is required.
 * @param isReadOnly Whether the input field is read-only.
 * @param onChangeCallback A closure representing the callback function to be executed when the input value changes.
 *
 * @return A `ConfiguratorEntry` object representing the decimal user input field with the specified configurations.
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputDecimal(id, label, isRequired, isReadOnly),
			onChangeCallback
	)
}

ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, String formatType, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputDecimal(id, label, isRequired, isReadOnly, formatType),
			onChangeCallback
	)
}

/**
 * Creates a `ConfiguratorEntry` object for a decimal user input field with an `onChange` callback and a default value.
 *
 * @param id The unique identifier for the input field.
 * @param label The label or prompt for the input field.
 * @param isRequired Whether the input field is required.
 * @param isReadOnly Whether the input field is read-only.
 * @param defaultValue The default value to be used if the input value is null
 * @param onChangeCallback A closure representing the callback function to be executed when the input value changes.
 *
 * @return A `ConfiguratorEntry` object representing the decimal user input field with the specified configurations.
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, BigDecimal defaultValue, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputDecimal(id, label, isRequired, isReadOnly, defaultValue),
			onChangeCallback
	)
}

/**
 * Creates a `ConfiguratorEntry` object for a decimal user input field with an `onChange` callback and a default value.
 *
 * @param id The unique identifier for the input field.
 * @param label The label or prompt for the input field.
 * @param isRequired Whether the input field is required.
 * @param isReadOnly Whether the input field is read-only.
 * @param defaultValue The default value to be used if the input value is null
 * @param formatType The format type for the decimal number (optional). Possible values are "Percent" and "###.000".
 * @param onChangeCallback A closure representing the callback function to be executed when the input value changes.
 *
 * @return A `ConfiguratorEntry` object representing the decimal user input field with the specified configurations.
 */
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, boolean isReadOnly, BigDecimal defaultValue, String formatType, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputDecimal(id, label, isRequired, isReadOnly, defaultValue, formatType),
			onChangeCallback
	)
}

/**
 * Creates an input entry for a decimal number with the specified properties.
 *
 * @param id The ID of the input element.
 * @param label The label of the input element.
 * @param isRequired A boolean value indicating whether the input element is required or not.
 * @param defaultValue The default value for the input element (optional).
 * @param formatType The format type for the decimal number (optional). Possible values are "Percent" and "###.000".
 * @return A ConfiguratorEntry object containing the input element with the specified properties.
 */
@Deprecated
ConfiguratorEntry createInputDecimal(String id, String label, boolean isRequired, defaultValue, String formatType) {
	def entry = createInput(id, InputType.USERENTRY, label, isRequired, false)
	ContextParameter parameter = entry.getFirstInput()
	parameter.setConfigParameter("dataType", "float");
	parameter.setConfigParameter("formatType", formatType);

	if (parameter && !parameter.getValue() && defaultValue) {
		parameter.setValue(defaultValue)
	}
	return entry
}

/**
 * Creates a ConfiguratorEntry for a date input field with the specified configuration options.
 *
 * @param id the unique identifier for the input field
 * @param label the label for the input field
 * @param isRequired a boolean indicating if the input field is required
 * @param isReadOnly a boolean indicating if the input field is read-only
 * @return a ConfiguratorEntry for a date input field
 */
ConfiguratorEntry createInputDate(String id, String label, boolean isRequired, boolean isReadOnly) {
	return createInput(id, InputType.DATEUSERENTRY, label, isRequired, isReadOnly)
}

/**
 * Creates a ConfiguratorEntry for a date input field with the specified configuration options.
 *
 * @param id the unique identifier for the input field
 * @param label the label for the input field
 * @param isRequired a boolean indicating if the input field is required
 * @param isReadOnly a boolean indicating if the input field is read-only
 * @param defaultValue the default value for the input field
 * @return a ConfiguratorEntry for a date input field
 */
ConfiguratorEntry createInputDate(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue) {
	return addDefaultValueOnNull(
			createInputDate(id, label, isRequired, isReadOnly),
			defaultValue
	)
}

/**
 * Creates a ConfiguratorEntry for a date input field with the specified configuration options.
 *
 * @param id the unique identifier for the input field
 * @param label the label for the input field
 * @param isRequired a boolean indicating if the input field is required
 * @param isReadOnly a boolean indicating if the input field is read-only
 * @param defaultValue the default value for the input field
 * @param onChangeCallback A closure representing the callback function to be executed when the input value changes.
 * @return a ConfiguratorEntry for a date input field
 */
ConfiguratorEntry createInputDate(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputDate(id, label, isRequired, isReadOnly, defaultValue),
			onChangeCallback
	)
}

/**

 * Creates a ConfiguratorEntry object with an input of type boolean user entry, and adds a default value if the existing value is null.
 * @param id The identifier for the input checkbox
 * @param label The label to be displayed next to the checkbox
 * @param isRequired Indicates whether the checkbox is required to be checked by the user
 * @param isReadOnly Indicates whether the checkbox is read-only and cannot be checked by the user
 * @param defaultValue The default value to use if the existing value is null
 * @return A ConfiguratorEntry object with a boolean user entry input, and a default value if necessary
 */
ConfiguratorEntry createInputCheckbox(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue) {
	def entry = createInput(id, InputType.BOOLEANUSERENTRY, label, isRequired, isReadOnly)
	if (entry.getFirstInput() && entry.getFirstInput().getValue() == null) {
		entry.getFirstInput().setValue(defaultValue)
	}
	return entry
}


/**

 * Creates an input of type OPTION with a dropdown menu of given options.
 * @param id The unique identifier of the input
 * @param label The label to display next to the input
 * @param isRequired Whether the input is required or not
 * @param isReadOnly Whether the input is read-only or editable
 * @param options A list of options to display in the dropdown menu
 * @return A ConfiguratorEntry object representing the created input
 */
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, List options) {
	def entry = createInput(id, InputType.OPTION, label, isRequired, isReadOnly)
	def dropdown = entry.getFirstInput()
	if (!dropdown || !options) {
		dropdown.setValue(null)
		return entry
	}

	dropdown.setValueOptions(options)

	// validate value exist on options list
	if (dropdown.getValue()) {
		def isValueInvalid = !options.contains(dropdown.getValue())
		api.logInfo("${id} is has invalid value:" as String, isValueInvalid)
		if (isValueInvalid) dropdown.setValue(null)
	}
	if (id == "caliper") api.logInfo("caliper json", api.jsonEncode(entry, false))
	return entry
}

/**

 * Creates an input of type OPTION with a dropdown menu of given options.
 * @param id The unique identifier of the input
 * @param label The label to display next to the input
 * @param isRequired Whether the input is required or not
 * @param isReadOnly Whether the input is read-only or editable
 * @param options A list of options to display in the dropdown menu
 * @param defaultValue The default value to use if the existing value is null
 * @return A ConfiguratorEntry object representing the created input
 */
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, List options, defaultValue) {
	return addDefaultValueOnNull(
			createInputOption(id, label, isRequired, isReadOnly, options),
			defaultValue
	)
}

/**

 * Creates an input of type OPTION with a dropdown menu of given options.
 * @param id The unique identifier of the input
 * @param label The label to display next to the input
 * @param isRequired Whether the input is required or not
 * @param isReadOnly Whether the input is read-only or editable
 * @param options A list of options to display in the dropdown menu
 * @param defaultValue The default value to use if the existing value is null
 * @param onChangeCallback The callback function to execute when the value of the input element changes.
 * @return A ConfiguratorEntry object representing the created input
 */
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, List options, defaultValue, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputOption(id, label, isRequired, isReadOnly, options),
			onChangeCallback
	)
}

/**

 * Creates an input of type OPTION with a dropdown menu of given options.
 * @param id The unique identifier of the input
 * @param label The label to display next to the input
 * @param isRequired Whether the input is required or not
 * @param isReadOnly Whether the input is read-only or editable
 * @param options A list of options to display in the dropdown menu
 * @param defaultValue The default value to use if the existing value is null
 * @param defaultWhenReadOnly Default the value when it´s read only
 * @return A ConfiguratorEntry object representing the created input
 */
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, List options, defaultValue, boolean defaultWhenReadOnly) {
	if (defaultWhenReadOnly) {
		return addDefaultValueOnNull(
				createInputOption(id, label, isRequired, isReadOnly, options),
				defaultValue
		)
	} else {
		return addDefaultValueOnNullWithoutReadOnly(
				createInputOption(id, label, isRequired, isReadOnly, options),
				defaultValue
		)
	}

}

/**

 * Creates an input of type OPTION with a dropdown menu of given options.
 * @param id The unique identifier of the input
 * @param label The label to display next to the input
 * @param isRequired Whether the input is required or not
 * @param isReadOnly Whether the input is read-only or editable
 * @param options A list of options to display in the dropdown menu
 * @return A ConfiguratorEntry object representing the created input
 */
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, List options, boolean isHide) {
	def entry = createInputOption(id, label, isRequired, isReadOnly, options)
	def switchType = isHide ? InputType.HIDDEN : InputType.OPTION
	if (isHide) entry.getFirstInput().setValue(null)
	entry.getFirstInput().applyType(switchType)

	return entry
}

/**

 * Creates an input of type OPTION with a dropdown menu of given options.
 * @param id The unique identifier of the input
 * @param label The label to display next to the input
 * @param isRequired Whether the input is required or not
 * @param isReadOnly Whether the input is read-only or editable
 * @param defaultValue The default value to set if none is provided or if the provided value is invalid
 * @param options A list of options to display in the dropdown menu
 * @return A ConfiguratorEntry object representing the created input
 */
@Deprecated
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, List options) {
	def entry = createInput(id, InputType.OPTION, label, isRequired, isReadOnly)
	def dropdown = entry.getFirstInput()
	if (!dropdown || !options) return entry

	dropdown.setValueOptions(options)

	// validate value exist on options list
	if (dropdown.getValue()) {
		def isValueInvalid = !options.contains(dropdown.getValue())
		if (isValueInvalid) dropdown.setValue(null)
	}
	// validate default exist on options list, and set
	if (defaultValue && dropdown.getValue() == null) {
		def isDefaultInvalid = !options.contains(defaultValue)
		def x = isDefaultInvalid
				? null
				: defaultValue
		dropdown.setValue(x)
	}

	return entry
}

/**
 * Creates an option input field with the given ID, label, required flag, read-only flag, default value, and options.
 * @param id The ID of the input field.
 * @param label The label of the input field.
 * @param isRequired A flag indicating whether the input field is required.
 * @param isReadOnly A flag indicating whether the input field is read-only.
 * @param defaultValue The default value for the input field.
 * @param options The available options for the input field.
 * @return A ConfiguratorEntry representing the created input field.
 */
ConfiguratorEntry createInputOption(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, Map options) {
	def entry = createInput(id, InputType.OPTION, label, isRequired, isReadOnly)
	def dropdown = entry.getFirstInput()

	if (entry.getFirstInput() && entry.getFirstInput().getValue() == null && defaultValue)
		entry.getFirstInput().setValue(defaultValue)

	if (options) {
		String[] x = options.keySet().collect { it.toString() }.toArray(new String[0])

		dropdown.setValueOptions(x)
		dropdown.setConfigParameter("labels", options)
	}
	return entry
}

/**
 * Creates a ConfiguratorEntry with an options list input field.
 *
 * @param id the ID of the input field
 * @param label the label of the input field
 * @param isRequired true if the input field is required, false otherwise
 * @param isReadOnly true if the input field is read-only, false otherwise
 * @param defaultValue the default value of the input field
 * @param options the list of available options for the input field
 * @param onChangeCallback The callback function to execute when the value of the input element changes.
 * @return the ConfiguratorEntry with the options list input field
 */
ConfiguratorEntry createInputOptions(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, List options, Closure onChangeCallback) {
	def entry = createInput(id, InputType.OPTIONS, label, isRequired, isReadOnly)

	if (options) {
		entry.getFirstInput().setValueOptions(options as List<String>)
	}

	if (entry.getFirstInput() && entry.getFirstInput().getValue() == null) {
		entry.getFirstInput().setValue(defaultValue)
	}

	return entry
}

/**
 * Creates a ConfiguratorEntry with an options list input field.
 *
 * @param id the ID of the input field
 * @param label the label of the input field
 * @param isRequired true if the input field is required, false otherwise
 * @param isReadOnly true if the input field is read-only, false otherwise
 * @param defaultValue the default value of the input field
 * @param options the list of available options for the input field
 * @return the ConfiguratorEntry with the options list input field
 */
ConfiguratorEntry createInputOptions(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, List options) {
	def entry = createInput(id, InputType.OPTIONS, label, isRequired, isReadOnly)

	if (options) {
		entry.getFirstInput().setValueOptions(options as List<String>)
	}

	if (entry.getFirstInput() && entry.getFirstInput().getValue() == null) {
		entry.getFirstInput().setValue(defaultValue)
	}

	return entry
}

/**
 * Creates an OPTIONS input configurator entry.
 *
 * @param id the ID of the input
 * @param label the label of the input
 * @param isRequired whether the input is required
 * @param isReadOnly whether the input is read-only
 * @param defaultValue the default value of the input
 * @param options a map of option values and labels
 * @return the configurator entry
 */
ConfiguratorEntry createInputOptions(String id, String label, boolean isRequired, boolean isReadOnly, defaultValue, Map options) {
	def entry = createInput(id, InputType.OPTIONS, label, isRequired, isReadOnly)
	def dropdown = entry.getFirstInput()

	if (options) {
		dropdown.setValueOptions(options.keySet() as List<String>)
		dropdown.setConfigParameter("labels", options)
	}

	if (dropdown.getValue() == null) {
		dropdown.setValue(defaultValue)
	}

	return entry
}

/**
 * Creates a CUSTOMER input configurator entry.
 *
 * @param id the ID of the input
 * @param label the label of the input
 * @param isRequired whether the input is required
 * @param isReadOnly whether the input is read-only
 * @param filterFormula the URL of the formula
 * @param params a map of param values
 * @return the configurator entry
 */
ConfiguratorEntry createInputCustomer(String id, String label, boolean isRequired, boolean isReadOnly, String filterFormula, Map params) {
	def entry = api.createConfiguratorEntry()

	def filterFormulaParams = api.jsonEncode(params)

	def param = api.inputBuilderFactory().createCustomerEntry(id)
			.setLabel(label)
			.setRequired(isRequired)
			.setReadOnly(isReadOnly)
			.setFilterFormulaName(filterFormula)
			.setFilterFormulaParam(filterFormulaParams)
			.buildContextParameter()
	entry.createParameter(param)

	return entry
}

/**
 * Creates a CUSTOMER input configurator entry.
 *
 * @param id the ID of the input
 * @param label the label of the input
 * @param isRequired whether the input is required
 * @param isReadOnly whether the input is read-only
 * @param filterFormula the URL of the formula
 * @param params a map of param values
 * @param onChangeCallback The callback function to execute when the value of the input element changes.
 * @return the configurator entry
 */
ConfiguratorEntry createInputCustomer(String id, String label, boolean isRequired, boolean isReadOnly, String filterFormula, Map params, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputCustomer(id, label, isRequired, isReadOnly, filterFormula, params),
			onChangeCallback
	)
}

/**
 * Creates a CUSTOMER GROUP input configurator entry.
 *
 * @param id the ID of the input
 * @param label the label of the input
 * @param isRequired whether the input is required
 * @param isReadOnly whether the input is read-only
 * @param filterFormula the URL of the formula
 * @param params a map of param values
 * @return the configurator entry
 */
ConfiguratorEntry createInputCustomerGroup(String id, String label, boolean isRequired, boolean isReadOnly, String filterFormula, Map params) {
	def entry = api.createConfiguratorEntry()

	def filterFormulaParams = api.jsonEncode(params)

	def param = api.inputBuilderFactory().createCustomerGroupEntry(id)
			.setLabel(label)
			.setRequired(isRequired)
			.setReadOnly(isReadOnly)
			.setFilterFormulaName(filterFormula)
			.setFilterFormulaParam(filterFormulaParams)
			.buildContextParameter()
	entry.createParameter(param)

	return entry
}

/**
 * Creates a CUSTOMER GROUP input configurator entry.
 *
 * @param id the ID of the input
 * @param label the label of the input
 * @param isRequired whether the input is required
 * @param isReadOnly whether the input is read-only
 * @param filterFormula the URL of the formula
 * @param params a map of param values
 * @param onChangeCallback The callback function to execute when the value of the input element changes.
 * @return the configurator entry
 */
ConfiguratorEntry createInputCustomerGroup(String id, String label, boolean isRequired, boolean isReadOnly, String filterFormula, Map params, Closure onChangeCallback) {
	return addCallbackConfigurationOnChange(
			createInputCustomerGroup(id, label, isRequired, isReadOnly, filterFormula, params),
			onChangeCallback
	)
}

/**
 * Creates a PRODUCT GROUP input configurator entry.
 *
 * @param id the ID of the input
 * @param label the label of the input
 * @param isRequired whether the input is required
 * @param isReadOnly whether the input is read-only
 * @param filterFormula the URL of the formula
 * @param params a map of param values
 * @return the configurator entry
 */
ConfiguratorEntry createInputProductGroup(String id, String label, boolean isRequired, boolean isReadOnly, String filterFormula, Map params) {
    def entry = api.createConfiguratorEntry()

    def filterFormulaParams = api.jsonEncode(params)

    def param = api.inputBuilderFactory().createProductGroupEntry(id)
            .setLabel(label)
            .setRequired(isRequired)
            .setReadOnly(isReadOnly)
            .setFilterFormulaName(filterFormula)
            .setFilterFormulaParam(filterFormulaParams)
            .buildContextParameter()
    entry.createParameter(param)

    return entry
}

/**
 * Create a column definition for a matrix input table.
 * @param String id of the column
 * @param String label of the column
 * @param FieldFormatType type format of the column
 * @param boolean readOnlyColumns
 * @return Input Matrix column configuration to be used in createInputMatrix
 */
def createInputMatrixColumn(String id, String label, FieldFormatType type, boolean readOnlyColumns) {
	return [id: id, label: label, type: type, readOnlyColumns: readOnlyColumns]
}

///**
// * @param id of the column
// * @param label of the column
// * @param type format of the column
// * @param readOnlyColumns
// * @param isPrimaryKey previous values and new ones will be merged by this field
// * @return Input Matrix column configuration
// */
//def createInputMatrixColumn(String id, String label, FieldFormatType type, boolean readOnlyColumns, boolean isPrimaryKey) {
//	return [id: id, label: label, type: type, readOnlyColumns: readOnlyColumns, isPrimaryKey: isPrimaryKey]
//}

/**
 * Creates a dropdown column configuration object for a matrix input field.
 *
 * @param {String} id - The unique identifier for the dropdown column.
 * @param {String} label - The label for the dropdown column.
 * @param {FieldFormatType} type - The type of the dropdown column, such as string or number.
 * @param {boolean} readOnlyColumns - Indicates whether the column should be read-only or editable.
 * @param {List<Object>} columnValueOptions - The list of options for the dropdown column.
 * @returns {Object} - The dropdown column configuration object.
 */

def createInputMatrixDropdownColumn(String id, String label, FieldFormatType type, boolean readOnlyColumns, List<Object> columnValueOptions) {
	return [id: id, label: label, type: type, readOnlyColumns: readOnlyColumns, columnValueOptions: columnValueOptions]
}

/**
 * Creates an options configuration object for a matrix input field.
 *
 * @param {boolean} canModifyRows - Indicates whether the rows can be modified.
 * @param {boolean} disableRowSelection - Indicates whether row selection is disabled.
 * @param {boolean} noCellRefresh - Indicates whether the cells should not be refreshed.
 * @param {boolean} noRefresh - Indicates whether the matrix should not be refreshed.
 * @param {boolean} enableClientFilter - Indicates whether client-side filtering is enabled.
 * @returns {Object} - The options configuration object.
 */
def createInputMatrixOptions(boolean canModifyRows, boolean disableRowSelection, boolean noCellRefresh, boolean noRefresh, boolean enableClientFilter) {
	return [
			canModifyRows: canModifyRows, disableRowSelection: disableRowSelection, noCellRefresh: noCellRefresh, noRefresh: noRefresh, enableClientFilter: enableClientFilter
	]
}

/**
 * @param id of the matrix
 * @param label of the matrix
 * @param matrixColumns create this parameter with  createInputMatrixColumn method
 * @param matrixOptions create this parameter with createInputMatrixOptions method
 * @param defaults list of map. Ex: [["columnA": 1.5, "columnB": 2.5],["columnA": 0.5, "columnB": -2.5]]
 * @return Input Matrix
 */
ConfiguratorEntry createInputMatrix(String id, String label, matrixColumns, matrixOptions, defaults) {
	def columnsIds = matrixColumns.collect { it.id as String }
	def columnsLabels = matrixColumns.collect { it.label as String }
	def readOnlyColumns = matrixColumns.findAll { it.readOnlyColumns }.collect { it.id as String }
	def columnsTypes = matrixColumns.collect { it.type ? getCode(it.type) : null }
	def columnValueOptions = matrixColumns.findAll { it.columnValueOptions }.collectEntries { [(it.id): it.columnValueOptions] }
//	String primaryKey = matrixColumns.findAll { it.isPrimaryKey }.collect { it.id }.find()
	return createInputMatrix(
			id,
			label,
			columnsIds,
			columnsLabels,
			readOnlyColumns,
			columnValueOptions,
			columnsTypes,
//			primaryKey,
			matrixOptions.canModifyRows,
			matrixOptions.disableRowSelection,
			matrixOptions.noCellRefresh,
			matrixOptions.noRefresh,
			matrixOptions.enableClientFilter,
			defaults)
}

/**
 *
 * @param String id
 * @param String label
 * @param List<String> columnsIds
 * @param columnsLabels
 * @param readOnlyColumns
 * @param columnValueOptions
 * @param List columnsTypes
 * @param boolean canModifyRows
 * @param boolean disableRowSelection
 * @param boolean noCellRefresh
 * @param boolean noRefresh
 * @param boolean enableClientFilter
 * @param defaults
 * @return
 */
ConfiguratorEntry createInputMatrix(
		String id, String label, List<String> columnsIds, List<String> columnsLabels, List<String> readOnlyColumns,
		columnValueOptions, List<Object> columnsTypes, boolean canModifyRows, boolean disableRowSelection,
		boolean noCellRefresh, boolean noRefresh, boolean enableClientFilter, defaults) {
	ConfiguratorEntry matrix = createInput(id, InputType.INPUTMATRIX, label, false, false)
	def input = matrix.getFirstInput()
	input.addParameterConfigEntry("columns", columnsIds)
	input.addParameterConfigEntry("columnLabels", columnsLabels)
	input.addParameterConfigEntry("columnType", columnsTypes)
	input.addParameterConfigEntry("readOnlyColumns", readOnlyColumns)
	input.addParameterConfigEntry("columnValueOptions", columnValueOptions)
	input.addParameterConfigEntry("canModifyRows", canModifyRows)
	input.addParameterConfigEntry("disableRowSelection", disableRowSelection)
	input.addParameterConfigEntry("noCellRefresh", noCellRefresh)
	input.addParameterConfigEntry("noRefresh", noRefresh)
	input.addParameterConfigEntry("enableClientFilter", enableClientFilter)
	// input.setHiddenActions(InputButtonAction.ALL)
	def previousValue = input.getValue()

	if (defaults) input.setValue(defaults)
	if (previousValue) input.setValue(previousValue)

	matrix
}

/**
 *
 * @param String id
 * @param String label
 * @param matrixColumns
 * @param matrixOptions
 * @param defaults
 * @param onChangeCallback
 * @return
 */
ConfiguratorEntry createInputMatrix(String id, String label, matrixColumns, matrixOptions, defaults, onChangeCallback) {
	ConfiguratorEntry matrix = createInputMatrix(id, label, matrixColumns, matrixOptions, defaults)
	def input = matrix.getFirstInput()
	def previousValue = input.getValue()
	def columns = matrixColumns.id

	// keep track on changes
	ContextParameter isChanged = matrix.createParameter(InputType.HIDDEN, id + "IsChanged")
	ContextParameter hiddenPrevious = matrix.createParameter(InputType.HIDDEN, id + "Previous")

	if (isChanged?.getValue() == null && hiddenPrevious.getValue() == null) {
		isChanged.setValue(false)
		hiddenPrevious.setValue(previousValue)
	} else {
		boolean hasChange = filterMapByFieldsList(previousValue, columns) != filterMapByFieldsList(hiddenPrevious.getValue(), columns)

		isChanged.setValue(hasChange)
		if (hasChange) {
			hiddenPrevious.setValue(previousValue)
			onChangeCallback(input.getValue())
		}
	}

	return matrix
}


/**
 * Deprecated use setEntryInputsInColumns
 * This method creates a collapse layout with multiple rows and columns based on the given parameters.
 *
 * @param label the label for the collapse layout
 * @param columns the number of columns to create in each row
 * @param inputs a List of ContextParameters to be used as input for the rows
 * @return a ContextParameter representing the collapse layout with the given inputs distributed in rows and columns
 */
@Deprecated
def createCollapseRowLayout(String label, int columns, List<ContextParameter> inputs) {
	if (columns <= 0) return null

	def filterInputs = inputs.findAll({ input -> input?.getInputType() != InputType.HIDDEN })

	def rowsQty = filterInputs.size() < columns ? 1 : filterInputs.size() / columns

	def entry = api.inputBuilderFactory()
			.createCollapseLayout(label)
			.setLabel(label)
	def emptyColumn = api.createConfiguratorEntry(InputType.HIDDEN, "EmptyRow").getFirstInput()

	for (int i = 0; i < rowsQty; i++) { //0,1,2
		def rowName = "Row$i" as String
		def builderFactory = api.inputBuilderFactory().createRowLayout(rowName)

		for (int j = 0; j < columns; j++) {
			if (!filterInputs || filterInputs.size() == 0) {
				builderFactory.addInput(emptyColumn)
				continue
			}
			def added = filterInputs.reverse(true).pop()
			filterInputs.reverse(true)
			if (added) builderFactory.addInput(added)
			else builderFactory.addInput(emptyColumn)
		}
		entry.addInput(builderFactory.buildContextParameter())

		// This should never happen, if it does, solve it.
		if (rowsQty > 999) {
			api.throwException("BDP Lib, UserInput createCollapseRowLayout exceeded max rows allowed")
			break;
		}
	}
	return entry.buildContextParameter()
}

/**
 * @param ConfiguratorEntry entry
 * @param int columns
 * @param List<ContextParameter> inputs
 */
void setEntryInputsInColumns(ConfiguratorEntry entry, int columns, List<ContextParameter> inputs) {
	if (columns <= 0) {
		api.throwException("Bdp lib error, user input setEntryInputsInColumns need a number of row greater than zero!")
	}
	final rowSuffix = "Row"
	def previousRowName = entry.getInputs().findAll { it.name.contains(rowSuffix) }.reverse()?.find()?.name

	int offset = 0
	if (previousRowName) {
		def previousRowCount = (previousRowName - rowSuffix) as int
		offset = previousRowCount + 1
	}

	def visibleInputs = inputs.findAll()
			.findAll { input -> input?.getInputType() != InputType.HIDDEN }

	def hiddenInputs = inputs.findAll()
			.findAll { input -> input?.getInputType() == InputType.HIDDEN }

	if (hiddenInputs) {
		def rowLayoutHidden = api.inputBuilderFactory().createRowLayout("hiddenInputs")
		for (hidden in hiddenInputs) {
			rowLayoutHidden.addInput(hidden)
		}
		rowLayoutHidden.addToConfiguratorEntry(entry)
	}

	if (!visibleInputs) {
		return
	}

	def rowsQty = visibleInputs.size() < columns
			? 1
			: visibleInputs.size() / columns

	if (rowsQty > 999) {
		api.throwException("BDP Lib, UserInput createCollapseRowLayout exceeded max rows allowed")
	}

	def emptyColumn = api.createConfiguratorEntry(InputType.ROW, "EmptyRow").getFirstInput()

	for (int i = 0; i < rowsQty; i++) { //0,1,2
		def rowName = "$rowSuffix${i + offset}" as String
		def rowLayout = api.inputBuilderFactory().createRowLayout(rowName)

		for (int j = 0; j < columns; j++) {
			if (!visibleInputs || visibleInputs.size() == 0) {
				rowLayout.addInput(emptyColumn)
				continue
			}
			def added = visibleInputs.reverse(true).pop()
			visibleInputs.reverse(true)
			rowLayout.addInput(added ?: emptyColumn)
		}

		// other methods ignore inputs when they value change to null
		entry.addParameter(rowLayout.buildContextParameter())
	}
}

void setEntryInputsInColumns(ConfiguratorEntry entry, String groupLabel, int columns, List<ContextParameter> inputs) {
	if (columns <= 0) {
		api.throwException("Bdp lib error, user input setEntryInputsInColumns need a number of row greater than zero!")
	}

	def collapseInputBuilder = api.inputBuilderFactory()
			.createCollapseLayout(groupLabel)
			.setLabel(groupLabel)

	final rowSuffix = "Row"
	def previousRowName = entry.getInputs().findAll { it.name.contains(rowSuffix) }.reverse()?.find()?.name

	int offset = 0
	if (previousRowName) {
		def previousRowCount = (previousRowName - rowSuffix) as int
		offset = previousRowCount + 1
	}

	def visibleInputs = inputs.findAll()
			.findAll { input -> input?.getInputType() != InputType.HIDDEN }

	def hiddenInputs = inputs.findAll()
			.findAll { input -> input?.getInputType() == InputType.HIDDEN }

	if (hiddenInputs) {
		def rowHiddenBuilder = api.inputBuilderFactory().createRowLayout("hiddenInputs")
		for (hidden in hiddenInputs) {
			rowHiddenBuilder.addInput(hidden)
		}
		collapseInputBuilder.addInput(rowHiddenBuilder.buildContextParameter())
	}

	if (!visibleInputs) {
		return
	}

	def rowsQty = visibleInputs.size() < columns
			? 1
			: visibleInputs.size() / columns

	if (rowsQty > 999) {
		api.throwException("BDP Lib, UserInput createCollapseRowLayout exceeded max rows allowed")
	}

	def emptyColumn = api.createConfiguratorEntry(InputType.ROW, "EmptyRow").getFirstInput()

	for (int i = 0; i < rowsQty; i++) { //0,1,2
		def rowName = "$rowSuffix${i + offset}" as String
		def rowInputBuilder = api.inputBuilderFactory().createRowLayout(rowName)

		for (int j = 0; j < columns; j++) {
			if (!visibleInputs || visibleInputs.size() == 0) {
				rowInputBuilder.addInput(emptyColumn)
				continue
			}
			def added = visibleInputs.reverse(true).pop()
			visibleInputs.reverse(true)
			rowInputBuilder.addInput(added ?: emptyColumn)
		}

		collapseInputBuilder.addInput(rowInputBuilder.buildContextParameter())

		// other methods ignore inputs when they value change to null
	}
	entry.addParameter(collapseInputBuilder.buildContextParameter())

}

/**
 Parameters: String id used in the Header where the Configurator is created, "params" map
 Return: The ConfiguratorEntry where the value is available in every calculation, this object should be returned by the element.

 Store the configurator param
 For some reason the configurators params are available in the first calculate, when the user recalculate the quote,
 the value obtained by api.input will be null, this is solved by storing the value in the logic element
 PriceFx automatically persist values in elements when logic recalculate.

 Store method needs to be used in a individual element(quote configurator) and the logic must return the result of this method to work properly

 Example
 Header:
 def configuratorPreviousValue = quoteProcessor.getHelper().getRoot().getInputByName("nameConfigurator")?.value ?: [:]
 def params = [param1: "parameter value"]
 quoteProcessor.addOrUpdateInput(
 "ROOT", [
 "name"           : "nameConfigurator",
 "label"          : "label",
 "url"            : "url",
 "type"           : InputType.INLINECONFIGURATOR,
 "value"          : previousValues + params,
 "parameterConfig": [:]
 ])
 Configurator
 StoreElement
 ConfiguratorEntry store = libs.BdpLib.UserInputs.store("param1")
 api.local.value = store.getFirstInput().getValue()

 return store
 */
ConfiguratorEntry store(String id) {
	ConfiguratorEntry entry = api.createConfiguratorEntry()
	ContextParameter parameter = entry.createParameter(InputType.HIDDEN, id)

	def previousValue = api.input(id)
	if (previousValue) parameter.setValue(previousValue)
	return entry
}

