import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "1"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["ReferencePeriod"] as Map : [:]
def defaultValue = options?.size() == 1 ? options?.keySet()?.find() : null

if (havePermissions && shouldShow) {
    entry = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID,
            lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_LABEL,
            true,
            false,
            defaultValue,
            options as Map
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID,
            InputType.HIDDEN,
            lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_LABEL,
            false,
            true
    )
    if (!shouldShow) entry?.getFirstInput()?.setValue(null)
}

return havePermissions ? null : entry