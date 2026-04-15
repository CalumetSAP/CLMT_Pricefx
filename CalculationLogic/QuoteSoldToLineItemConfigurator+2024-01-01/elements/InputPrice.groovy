import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def required = priceType == "2" || priceType == "1"
def readOnly = priceType == "3" || priceType == "4"

def defaultValue = api.local.shouldUseDefault ? (api.local.contractData?.Price ? api.local.contractData?.Price as BigDecimal : null) : null

if (havePermissions) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.PRICE_ID,
            lineItemConstants.PRICE_LABEL,
            required,
            readOnly,
            defaultValue
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PRICE_ID,
            InputType.HIDDEN,
            lineItemConstants.PRICE_LABEL,
            false,
            false
    )
    input = entry.getFirstInput()
}

return havePermissions ? null : entry