import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def required = priceType == "2" || priceType == "1"
def readOnly = priceType == "3" || priceType == "4"

if (havePermissions) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.DELIVERED_PRICE_ID,
            lineItemConstants.DELIVERED_PRICE_LABEL,
            required,
            readOnly
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.DELIVERED_PRICE_ID,
            InputType.HIDDEN,
            lineItemConstants.DELIVERED_PRICE_LABEL,
            false,
            false
    )
    input = entry.getFirstInput()
}

return havePermissions ? null : entry