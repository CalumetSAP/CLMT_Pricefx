import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = api.local.shouldShowFreightFields //&& InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = !(api.local.isFreightGroup || api.local.isPricingGroup)
def required = InputFreightEstimate?.input?.getValue() as Boolean

if (havePermissions && shouldShow) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.FREIGHT_AMOUNT_ID,
            lineItemConstants.FREIGHT_AMOUNT_LABEL,
            required,
            readOnly,
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

return havePermissions && shouldShow ? null : entry