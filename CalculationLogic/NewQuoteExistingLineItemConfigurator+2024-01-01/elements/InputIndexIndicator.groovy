import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "2"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isPricingGroup

def defaultValue = api.local.shouldUseDefault ? api.local.contractData?.IndexIndicator : null
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputCheckbox(
            lineItemConstants.INDEX_INDICATOR_ID,
            lineItemConstants.INDEX_INDICATOR_LABEL,
            required,
            false,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.INDEX_INDICATOR_ID,
            InputType.HIDDEN,
            lineItemConstants.INDEX_INDICATOR_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry