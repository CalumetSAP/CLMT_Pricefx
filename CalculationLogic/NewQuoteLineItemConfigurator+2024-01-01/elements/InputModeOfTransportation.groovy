import net.pricefx.common.api.InputType

def shouldShow = InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isFreightGroup as boolean
def options = api.local.modeOfTransportationOptions && !api.isInputGenerationExecution() ? api.local.modeOfTransportationOptions as Map : [:]
def defaultValue = options?.size() == 1 ? options?.keySet()?.find() : null
def entry = null
if (api.local.isPricingGroup || (api.local.isFreightGroup && shouldShow)) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.MODE_OF_TRANSPORTATION_ID,
            lineItemConstants.MODE_OF_TRANSPORTATION_LABEL,
            required,
            false,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.MODE_OF_TRANSPORTATION_ID,
            InputType.HIDDEN,
            lineItemConstants.MODE_OF_TRANSPORTATION_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry