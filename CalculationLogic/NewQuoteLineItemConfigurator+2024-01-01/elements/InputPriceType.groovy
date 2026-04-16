import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isSalesGroup || api.local.isPricingGroup
def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["PriceType"] as Map : [:]

if (api.local.isSoldToOnly) options.remove("1")

def defaultValue = options?.size() == 1 ? options?.keySet()?.find() : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PRICE_TYPE_ID,
            lineItemConstants.PRICE_TYPE_LABEL,
            required,
            false,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PRICE_TYPE_ID,
            InputType.HIDDEN,
            lineItemConstants.PRICE_TYPE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry