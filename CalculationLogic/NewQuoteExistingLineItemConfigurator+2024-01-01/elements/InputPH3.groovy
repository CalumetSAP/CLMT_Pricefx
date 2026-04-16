import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ph = api.local.product?.PH3Code ? api.local.product?.PH3Code + " - " + api.local.product?.PH3Description : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.PH3_ID,
            lineItemConstants.PH3_LABEL,
            false,
            true,
            ph as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PH3_ID,
            InputType.HIDDEN,
            lineItemConstants.PH3_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry