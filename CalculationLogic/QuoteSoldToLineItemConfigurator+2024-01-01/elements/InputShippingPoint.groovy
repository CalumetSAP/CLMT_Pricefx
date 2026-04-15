import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = (api.local.isFreightGroup && !api.local.isPricingGroup) || InputRejectionReason?.input?.getValue()
def options = out.FindPlantShippingPoint && !api.isInputGenerationExecution() ? out.FindPlantShippingPoint : []
def defaultValue = options?.size() == 1 ? options?.find() : null
if (!defaultValue) defaultValue = api.local.contractData?.ShippingPoint as String
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.SHIPPING_POINT_ID,
            lineItemConstants.SHIPPING_POINT_LABEL,
            true,
            readOnly,
            options as List,
            defaultValue,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SHIPPING_POINT_ID,
            InputType.HIDDEN,
            lineItemConstants.SHIPPING_POINT_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry