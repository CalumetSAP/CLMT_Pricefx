import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputNumber(
            lineItemConstants.MOQ_ID,
            lineItemConstants.MOQ_LABEL,
            true,
            readOnly
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.MOQ_ID,
            InputType.HIDDEN,
            lineItemConstants.MOQ_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry