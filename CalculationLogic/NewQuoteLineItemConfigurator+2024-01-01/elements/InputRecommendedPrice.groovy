import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.RECOMMENDED_PRICE_ID,
            lineItemConstants.RECOMMENDED_PRICE_LABEL,
            false,
            true
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.RECOMMENDED_PRICE_ID,
            InputType.HIDDEN,
            lineItemConstants.RECOMMENDED_PRICE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry