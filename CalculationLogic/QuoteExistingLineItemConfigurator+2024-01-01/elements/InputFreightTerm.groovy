import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.FREIGHT_TERM_ID,
            lineItemConstants.FREIGHT_TERM_LABEL,
            false,
            true,
            api.local.dataSourceData?.FreightTerm as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_TERM_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_TERM_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry