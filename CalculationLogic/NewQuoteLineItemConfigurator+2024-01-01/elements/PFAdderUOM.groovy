import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "1"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions : []
def defaultValue = "UG6"

if (havePermissions && shouldShow) {
    entry = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID,
            lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_LABEL,
            true,
            false,
            options as List,
            defaultValue
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_ID,
            InputType.HIDDEN,
            lineItemConstants.PF_CONFIGURATOR_ADDER_UOM_LABEL,
            false,
            true
    )
    if (!shouldShow) entry?.getFirstInput()?.setValue(null)
}

return havePermissions ? null : entry