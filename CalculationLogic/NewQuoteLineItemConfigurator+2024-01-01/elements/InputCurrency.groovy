import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def required = api.local.isPricingGroup && priceType != "4"
def readOnly = !required
def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["Currency"]?.values() : []
def defaultValue = options?.size() == 1 ? options?.find() : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.CURRENCY_ID,
            lineItemConstants.CURRENCY_LABEL,
            required,
            readOnly,
            options as List,
            defaultValue,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.CURRENCY_ID,
            InputType.HIDDEN,
            lineItemConstants.CURRENCY_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

def numberOfDecimals = InputNumberOfDecimals?.input?.getValue()
ContextParameter numberOfDecimalsHidden = out.NumberOfDecimalsHidden?.getFirstInput()
def numberOfDecimalsHiddenValue = numberOfDecimalsHidden?.getValue()
if (!numberOfDecimalsHiddenValue || numberOfDecimalsHiddenValue != numberOfDecimals) {
    numberOfDecimalsHidden?.setValue(numberOfDecimals)
    def updatedValue = null
    switch (numberOfDecimals) {
        case "2":
            updatedValue = options?.find { it == "USD" } ?: null
            break
        case "3":
            updatedValue = options?.find { it == "US3" } ?: null
            break
        case "4":
            updatedValue = options?.find { it == "US4" } ?: null
            break
    }
    input.setValue(updatedValue)
}

return entry