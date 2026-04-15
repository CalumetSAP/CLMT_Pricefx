import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.MATERIAL_PACKAGE_STYLE_ID,
            lineItemConstants.MATERIAL_PACKAGE_STYLE_LABEL,
            false,
            true,
            api.local.product?.MaterialPackageStyle as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.MATERIAL_PACKAGE_STYLE_ID,
            InputType.HIDDEN,
            lineItemConstants.MATERIAL_PACKAGE_STYLE_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry