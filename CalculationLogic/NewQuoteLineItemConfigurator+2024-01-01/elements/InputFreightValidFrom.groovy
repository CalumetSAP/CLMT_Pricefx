import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = api.local.shouldShowFreightFields //&& InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = InputFreightEstimate?.input?.getValue() && api.local.isPricingGroup
def readOnly = !(api.local.isPricingGroup || api.local.isFreightGroup)
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputDate(
            lineItemConstants.FREIGHT_VALID_FROM_ID,
            lineItemConstants.FREIGHT_VALID_FROM_LABEL,
            required,
            readOnly,
            api.local.validFromDate
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_VALID_FROM_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_VALID_FROM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry