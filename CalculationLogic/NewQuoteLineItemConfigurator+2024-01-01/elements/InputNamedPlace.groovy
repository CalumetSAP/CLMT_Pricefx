import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.NAMED_PLACE_ID,
            lineItemConstants.NAMED_PLACE_LABEL,
            false,
            readOnly
    ).getFirstInput()
    input?.setConfigParameter("maxLength", 25)
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.NAMED_PLACE_ID,
            InputType.HIDDEN,
            lineItemConstants.NAMED_PLACE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry