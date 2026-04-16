import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = !api.local.isSoldToOnly

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isPricingGroup as boolean
def readOnly = api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup
def options = api.local.shipToOptions && !api.isInputGenerationExecution() ? api.local.shipToOptions : []
def defaultValue = options?.size() == 1 ? options?.find() : null
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.SHIP_TO_ID,
            lineItemConstants.SHIP_TO_LABEL,
            required,
            readOnly,
            options,
            defaultValue,
            false
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
    if (!shouldShow) input?.setValue(null)
}

return entry