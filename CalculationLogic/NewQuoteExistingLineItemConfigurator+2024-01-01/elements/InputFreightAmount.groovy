import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = InputIncoterm?.input?.getValue() != "FCA" && InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = !api.local.isPricingGroup && !shouldShow
def required = shouldShow
def defaultValue = null

if (havePermissions) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.FREIGHT_AMOUNT_ID,
            lineItemConstants.FREIGHT_AMOUNT_LABEL,
            required,
            readOnly,
            defaultValue
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_AMOUNT_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_AMOUNT_LABEL,
            false,
            true
    )
    if (!shouldShow) entry?.getFirstInput()?.setValue(null)
}

return havePermissions ? null : entry