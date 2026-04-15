import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.DESCRIPTION_ID,
            lineItemConstants.DESCRIPTION_LABEL,
            false,
            true,
            api.local.product?.Description as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.DESCRIPTION_ID,
            InputType.HIDDEN,
            lineItemConstants.DESCRIPTION_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry