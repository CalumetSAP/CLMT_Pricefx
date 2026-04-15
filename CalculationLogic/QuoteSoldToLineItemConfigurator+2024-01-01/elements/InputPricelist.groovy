import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup
def shouldShow = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = out.FindPricelistOptions && !api.isInputGenerationExecution() ? out.FindPricelistOptions : [:]
def defaultValue = api.local.shouldUseDefault ? (api.local.contractData?.PriceListPLT as String ?: null) : null

def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PRICE_LIST_ID,
            lineItemConstants.PRICE_LIST_LABEL,
            false,
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