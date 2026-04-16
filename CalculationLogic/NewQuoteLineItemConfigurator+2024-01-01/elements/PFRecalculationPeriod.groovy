import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "1"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["RecalculationPeriod"] as Map : [:]
def defaultValue = options?.size() == 1 ? options?.keySet()?.find() : null
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID,
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_LABEL,
            true,
            false,
            defaultValue,
            options
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID,
            InputType.HIDDEN,
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry