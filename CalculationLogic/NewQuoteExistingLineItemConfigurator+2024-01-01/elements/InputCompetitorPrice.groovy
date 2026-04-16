import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def defaultValue = api.local.shouldUseDefault ? (api.local.contractData?.CompetitorPrice ? api.local.contractData?.CompetitorPrice as BigDecimal : null) : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.COMPETITOR_PRICE_ID,
            lineItemConstants.COMPETITOR_PRICE_LABEL,
            false,
            false,
            defaultValue
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