final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = out.FindSalesPersonOptions ?: [:]

input = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.SALES_PERSON_ID,
        headerConstants.SALES_PERSON_LABEL,
        false,
        false,
        null,
        options as Map
).getFirstInput()

return null