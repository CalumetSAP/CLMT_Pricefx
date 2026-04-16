import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.LEGACY_MATERIAL_NUMBER_ID,
            lineItemConstants.LEGACY_MATERIAL_NUMBER_LABEL,
            false,
            true,
            api.local.product?.LegacyMaterialNumber as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.LEGACY_MATERIAL_NUMBER_ID,
            InputType.HIDDEN,
            lineItemConstants.LEGACY_MATERIAL_NUMBER_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry