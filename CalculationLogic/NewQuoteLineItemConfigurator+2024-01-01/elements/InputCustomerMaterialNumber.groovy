import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID,
            lineItemConstants.CUSTOMER_MATERIAL_NUMBER_LABEL,
            false,
            false
    ).getFirstInput()
    input?.setConfigParameter("maxLength", 35)
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID,
            InputType.HIDDEN,
            lineItemConstants.CUSTOMER_MATERIAL_NUMBER_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry