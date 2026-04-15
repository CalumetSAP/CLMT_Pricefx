import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = !api.local.isSoldToOnly

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.shipTo && !api.isInputGenerationExecution() ? api.local.shipTo as Map : [:]
def defaultValue = options?.get(api.local.contractData?.ShipTo) as String
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.SHIP_TO_ID,
            lineItemConstants.SHIP_TO_LABEL,
            false,
            true,
            defaultValue
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