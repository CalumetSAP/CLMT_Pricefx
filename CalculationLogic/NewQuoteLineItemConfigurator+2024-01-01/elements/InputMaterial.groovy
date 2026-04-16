import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.MATERIAL_ID,
            lineItemConstants.MATERIAL_LABEL,
            false,
            true,
            api.local.product?.Material as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.MATERIAL_ID,
            InputType.HIDDEN,
            lineItemConstants.MATERIAL_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry