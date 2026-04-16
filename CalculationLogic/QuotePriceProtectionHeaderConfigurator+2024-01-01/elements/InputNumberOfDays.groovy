if (out.InputPriceProtection?.getFirstInput()?.getValue() != "Any Number of days from Announcement date" &&
        out.InputPriceProtection?.getFirstInput()?.getValue() != "Any Number of days from Effective date") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def entry = libs.BdpLib.UserInputs.createInputNumber(
        headerConstants.NUMBER_OF_DAYS_ID,
        headerConstants.NUMBER_OF_DAYS_LABEL,
        true,
        false
)

return entry