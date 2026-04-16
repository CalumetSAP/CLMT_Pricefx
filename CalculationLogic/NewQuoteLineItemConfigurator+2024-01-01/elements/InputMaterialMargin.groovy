import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.MATERIAL_MARGIN_ID,
            lineItemConstants.MATERIAL_MARGIN_LABEL,
            false,
            true
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.MATERIAL_MARGIN_ID,
            InputType.HIDDEN,
            lineItemConstants.MATERIAL_MARGIN_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry