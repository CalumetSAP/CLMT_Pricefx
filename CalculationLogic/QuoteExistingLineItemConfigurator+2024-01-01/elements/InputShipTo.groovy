import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.SHIP_TO_ID,
            lineItemConstants.SHIP_TO_LABEL,
            false,
            true,
            api.local.dataSourceData?.ShipTo as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SHIP_TO_ID,
            InputType.HIDDEN,
            lineItemConstants.SHIP_TO_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry