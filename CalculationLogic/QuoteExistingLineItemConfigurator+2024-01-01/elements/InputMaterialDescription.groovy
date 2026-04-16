import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.DESCRIPTION_ID,
            lineItemConstants.DESCRIPTION_LABEL,
            false,
            true,
            api.local.dataSourceData?.Description as String
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