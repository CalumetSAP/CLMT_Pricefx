import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.LINE_NUMBER_ID,
            lineItemConstants.LINE_NUMBER_LABEL,
            false,
            true,
            api.local.contractData?.SAPLineId as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.LINE_NUMBER_ID,
            InputType.HIDDEN,
            lineItemConstants.LINE_NUMBER_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry