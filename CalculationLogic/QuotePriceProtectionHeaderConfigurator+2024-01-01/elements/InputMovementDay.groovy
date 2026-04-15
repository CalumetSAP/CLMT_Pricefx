if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def required = out.InputPriceProtection?.getFirstInput()?.getValue() == "3"
def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
               "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
               "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.MOVEMENT_DAY_ID,
        headerConstants.MOVEMENT_DAY_LABEL,
        required,
        false,
        options
)

return entry