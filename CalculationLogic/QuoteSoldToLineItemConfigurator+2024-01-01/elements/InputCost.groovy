import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.COST_ID,
            lineItemConstants.COST_LABEL,
            false,
            true
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.COST_ID,
            InputType.HIDDEN,
            lineItemConstants.COST_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry