final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ce = api.configurator(
        lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME,
        lineItemConstants.NEW_QUOTE_CONFIGURATOR_URL,
        "80%",
        "80%"
)

return ce