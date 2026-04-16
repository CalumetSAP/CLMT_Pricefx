import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ph = api.local.product?.PH2Code ? api.local.product?.PH2Code + " - " + api.local.product?.PH2Description : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.PH2_ID,
            lineItemConstants.PH2_LABEL,
            false,
            true,
            ph as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PH2_ID,
            InputType.HIDDEN,
            lineItemConstants.PH2_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry