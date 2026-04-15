import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = !api.local.isSoldToOnly

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def value = out.FindShipToData && !api.isInputGenerationExecution() ? out.FindShipToData?.Country : null
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.SHIP_TO_COUNTRY_ID,
            lineItemConstants.SHIP_TO_COUNTRY_LABEL,
            false,
            true,
            value as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SHIP_TO_COUNTRY_ID,
            InputType.HIDDEN,
            lineItemConstants.SHIP_TO_COUNTRY_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry