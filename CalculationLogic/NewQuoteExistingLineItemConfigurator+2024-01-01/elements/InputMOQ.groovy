import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = (api.local.isFreightGroup && !api.local.isPricingGroup) || InputRejectionReason?.input?.getValue()
def defaultValue = input."MOQInput" ?: api.local.contractData?.MOQ as Integer
def entry = null
if (havePermissions) {
    if (defaultValue) {
        input = libs.BdpLib.UserInputs.createInputNumber(
                lineItemConstants.MOQ_ID,
                lineItemConstants.MOQ_LABEL,
                true,
                readOnly,
                defaultValue
        ).getFirstInput()
    } else {
        input = libs.BdpLib.UserInputs.createInputNumber(
                lineItemConstants.MOQ_ID,
                lineItemConstants.MOQ_LABEL,
                true,
                readOnly
        ).getFirstInput()
    }

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