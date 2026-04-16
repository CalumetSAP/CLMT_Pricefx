final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = [
        "Y": "Y",
        "N": "N"
]

input = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.LINE_REJECTED_ID,
        headerConstants.LINE_REJECTED_LABEL,
        false,
        false,
        null,
        options
).getFirstInput()

return null