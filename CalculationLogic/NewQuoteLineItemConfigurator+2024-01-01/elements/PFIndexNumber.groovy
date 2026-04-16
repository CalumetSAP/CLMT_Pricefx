import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "1"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = out.FindIndexValues && !api.isInputGenerationExecution() ? out.FindIndexValues as List : []
def defaultValue = options?.size() == 1 ? options?.find() : null

if (havePermissions && shouldShow) {
    entry = libs.BdpLib.UserInputs.createInputOptions(
            lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID,
            lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_LABEL,
            true,
            false,
            defaultValue,
            options as List
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID,
            InputType.HIDDEN,
            lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_LABEL,
            false,
            true
    )
    if (!shouldShow) entry?.getFirstInput()?.setValue(null)
}

if(entry?.getFirstInput()?.getValue()?.size() > 3) {
    entry?.getFirstInput()?.setValue(entry?.getFirstInput()?.getValue()?.take(3))
}

api.local.index1 = entry?.getFirstInput()?.getValue()?.size() > 0 ? entry?.getFirstInput()?.getValue()?.get(0) : null
api.local.index2 = entry?.getFirstInput()?.getValue()?.size() > 1 ? entry?.getFirstInput()?.getValue()?.get(1) : null
api.local.index3 = entry?.getFirstInput()?.getValue()?.size() > 2 ? entry?.getFirstInput()?.getValue()?.get(2) : null

return havePermissions ? null : entry