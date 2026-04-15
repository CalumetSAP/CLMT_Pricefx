import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def readOnly = priceType == "4"
def required = priceType == "2"
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDate(
            lineItemConstants.PRICE_VALID_TO_ID,
            lineItemConstants.PRICE_VALID_TO_LABEL,
            required,
            readOnly,
            api.local.validToDate
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PRICE_VALID_TO_ID,
            InputType.HIDDEN,
            lineItemConstants.PRICE_VALID_TO_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry