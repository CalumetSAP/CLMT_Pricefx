final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ce = api.configurator(
        lineItemConstants.CONFIGURATOR_NAME,
        lineItemConstants.CONFIGURATOR_URL,
        "80%",
        "80%"
)

return ce
