import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = (api.local.isFreightGroup && !api.local.isPricingGroup) || InputRejectionReason?.input?.getValue()
def options = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions : []
def defaultValue = api.local.contractData?.MOQUOM as String ?: (options.contains(api.local.product?.SalesUnit) ? api.local.product?.SalesUnit : null)
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.MOQ_UOM_ID,
            lineItemConstants.MOQ_UOM_LABEL,
            true,
            readOnly,
            options as List,
            defaultValue,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.MOQ_UOM_ID,
            InputType.HIDDEN,
            lineItemConstants.MOQ_UOM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry