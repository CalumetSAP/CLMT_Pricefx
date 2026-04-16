import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def options = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions : []
def required = priceType == "2"
def defaultValue = api.local.contractData?.PricingUOM as String ?: (options.contains(api.local.product?.UOM) ? api.local.product?.UOM : null)
def readOnly = priceType == "3" || priceType == "4"
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PRICING_UOM_ID,
            lineItemConstants.PRICING_UOM_LABEL,
            required,
            readOnly,
            options as List,
            defaultValue,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PRICING_UOM_ID,
            InputType.HIDDEN,
            lineItemConstants.PRICING_UOM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry