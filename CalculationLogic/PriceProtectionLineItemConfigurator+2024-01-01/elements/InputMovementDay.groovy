if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"
def readOnly = api.local.readOnly ? true : false//api.local.exclusionData ? true : false

def entry
if (api.local.exclusionData?.MovementDay) {
    entry = libs.BdpLib.UserInputs.createInputNumber(
            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_ID,
            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_LABEL,
            required,
            readOnly,
            api.local.exclusionData?.MovementDay as Integer
    )
} else {
    entry = libs.BdpLib.UserInputs.createInputNumber(
            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_ID,
            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_LABEL,
            required,
            readOnly
    )
}

return entry