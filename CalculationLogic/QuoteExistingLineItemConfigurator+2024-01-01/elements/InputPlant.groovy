import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.PLANT_ID,
            lineItemConstants.PLANT_LABEL,
            false,
            true,
            api.local.dataSourceData?.Plant as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PLANT_ID,
            InputType.HIDDEN,
            lineItemConstants.PLANT_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry