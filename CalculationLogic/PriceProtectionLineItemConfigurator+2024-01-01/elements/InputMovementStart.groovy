if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def required = out.InputMovementTiming?.getFirstInput()?.getValue() == "Quarter"
def readOnly = api.local.readOnly ? true : false//api.local.exclusionData ? true : false

def entry
if (api.local.exclusionData?.MovementStart) {
    entry = libs.BdpLib.UserInputs.createInputNumber(
            lineItem.PP_CONFIGURATOR_MOVEMENT_START_ID,
            lineItem.PP_CONFIGURATOR_MOVEMENT_START_LABEL,
            required,
            readOnly,
            api.local.exclusionData?.MovementStart as Integer
    )
} else {
    entry = libs.BdpLib.UserInputs.createInputNumber(
            lineItem.PP_CONFIGURATOR_MOVEMENT_START_ID,
            lineItem.PP_CONFIGURATOR_MOVEMENT_START_LABEL,
            required,
            readOnly
    )
}

return entry