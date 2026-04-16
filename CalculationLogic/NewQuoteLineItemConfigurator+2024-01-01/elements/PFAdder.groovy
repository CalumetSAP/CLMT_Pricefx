import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "1"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (havePermissions && shouldShow) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.PF_CONFIGURATOR_ADDER_ID,
            lineItemConstants.PF_CONFIGURATOR_ADDER_LABEL,
            true,
            false
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PF_CONFIGURATOR_ADDER_ID,
            InputType.HIDDEN,
            lineItemConstants.PF_CONFIGURATOR_ADDER_LABEL,
            false,
            false
    )
    if (!shouldShow) entry?.getFirstInput()?.setValue(null)
}

return havePermissions && shouldShow ? null : entry