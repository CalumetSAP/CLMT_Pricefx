import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()
def readOnly = priceType == "4"

def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.COMPETITOR_PRICE_ID,
            lineItemConstants.COMPETITOR_PRICE_LABEL,
            false,
            readOnly
    ).getFirstInput()
//            .setConfigParameter("noRefresh", true)
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.COMPETITOR_PRICE_ID,
            InputType.HIDDEN,
            lineItemConstants.COMPETITOR_PRICE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry