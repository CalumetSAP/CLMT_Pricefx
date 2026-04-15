final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ce = api.configurator(
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_URL,
        "80%",
        "80%"
)

return ce