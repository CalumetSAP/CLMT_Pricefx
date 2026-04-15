import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.LINE_NUMBER_ID,
            lineItemConstants.LINE_NUMBER_LABEL,
            false,
            true,
            api.local.dataSourceData?.SAPLineId as String
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