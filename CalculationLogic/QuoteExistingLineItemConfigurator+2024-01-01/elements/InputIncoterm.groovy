import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.INCO_TERM_ID,
            lineItemConstants.INCO_TERM_LABEL,
            false,
            true,
            api.local.dataSourceData?.Incoterm as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.INCO_TERM_ID,
            InputType.HIDDEN,
            lineItemConstants.INCO_TERM_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry