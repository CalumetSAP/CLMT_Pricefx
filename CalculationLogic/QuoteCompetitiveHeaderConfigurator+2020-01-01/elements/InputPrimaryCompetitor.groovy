final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.PRIMARY_COMPETITOR_ID,
        headerConstants.PRIMARY_COMPETITOR_LABEL,
        false,
        false,
        null,
        options
)

return entry