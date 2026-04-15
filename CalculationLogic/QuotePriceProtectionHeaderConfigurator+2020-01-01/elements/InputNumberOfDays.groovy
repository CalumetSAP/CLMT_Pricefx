if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return
if (out.InputPriceProtection?.getFirstInput()?.getValue() != "1" &&
        out.InputPriceProtection?.getFirstInput()?.getValue() != "2") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def entry = libs.BdpLib.UserInputs.createInputNumber(
        headerConstants.NUMBER_OF_DAYS_ID,
        headerConstants.NUMBER_OF_DAYS_LABEL,
        true,
        false
)

return entry