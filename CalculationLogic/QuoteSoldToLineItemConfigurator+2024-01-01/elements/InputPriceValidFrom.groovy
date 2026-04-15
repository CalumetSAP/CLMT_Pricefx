import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def readOnly = priceType == "4"
def required = priceType == "2"
def defaultValue = api.local.contractData?.PriceValidFrom ?: api.local.validFromDate
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDate(
            lineItemConstants.PRICE_VALID_FROM_ID,
            lineItemConstants.PRICE_VALID_FROM_LABEL,
            required,
            readOnly,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PRICE_VALID_FROM_ID,
            InputType.HIDDEN,
            lineItemConstants.PRICE_VALID_FROM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry