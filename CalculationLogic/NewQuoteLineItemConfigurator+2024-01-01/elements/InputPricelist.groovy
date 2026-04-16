import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def priceType = InputPriceType.input?.getValue()
def shouldShow = priceType == "3" || priceType == "2"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = out.FindPricelistOptions && !api.isInputGenerationExecution() ? out.FindPricelistOptions : [:]
def defaultValue = priceType == "3" ? (options?.size() == 1 ? options?.keySet()?.find() : null) : null
def required = priceType == "3"
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PRICE_LIST_ID,
            lineItemConstants.PRICE_LIST_LABEL,
            required,
            false,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PRICE_LIST_ID,
            InputType.HIDDEN,
            lineItemConstants.PRICE_LIST_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry