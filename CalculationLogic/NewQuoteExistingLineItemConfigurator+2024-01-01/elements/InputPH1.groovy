import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ph = api.local.product?.PH1Code ? api.local.product?.PH1Code + " - " + api.local.product?.PH1Description : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.PH1_ID,
            lineItemConstants.PH1_LABEL,
            false,
            true,
            ph as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PH1_ID,
            InputType.HIDDEN,
            lineItemConstants.PH1_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry