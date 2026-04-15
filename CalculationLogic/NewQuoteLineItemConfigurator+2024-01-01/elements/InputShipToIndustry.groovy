import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = !api.local.isSoldToOnly

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def value = out.FindShipToData && !api.isInputGenerationExecution() ? out.FindShipToData?.Industry : null
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.SHIP_TO_INDUSTRY_ID,
            lineItemConstants.SHIP_TO_INDUSTRY_LABEL,
            false,
            true,
            value as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SHIP_TO_INDUSTRY_ID,
            InputType.HIDDEN,
            lineItemConstants.SHIP_TO_INDUSTRY_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry