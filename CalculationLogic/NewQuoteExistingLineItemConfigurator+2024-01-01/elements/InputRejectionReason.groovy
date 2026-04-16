import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["RejectionReason"] as Map : [:]
def defaultValue = api.local.shouldUseDefault ? api.local.contractData?.RejectionReason : null

def entry = null
if (api.local.isPricingGroup) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.REJECTION_REASON_ID,
            lineItemConstants.REJECTION_REASON_LABEL,
            false,
            false,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.REJECTION_REASON_ID,
            InputType.HIDDEN,
            lineItemConstants.REJECTION_REASON_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry