final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = [
        "Any Number of days from Announcement date",
        "Any Number of days from Effective date",
        "Moves on a specified day"
]

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.PRICE_PROTECTION_ID,
        headerConstants.PRICE_PROTECTION_LABEL,
        false,
        false,
        options
)

return entry