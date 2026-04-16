import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ph = api.local.product?.PH4Code ? api.local.product?.PH4Code + " - " + api.local.product?.PH4Description : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.PH4_ID,
            lineItemConstants.PH4_LABEL,
            false,
            true,
            ph as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PH4_ID,
            InputType.HIDDEN,
            lineItemConstants.PH4_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry