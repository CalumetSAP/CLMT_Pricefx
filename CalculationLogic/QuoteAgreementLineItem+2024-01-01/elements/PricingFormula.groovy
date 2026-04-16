final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ce = api.configurator(
        lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME,
        lineItemConstants.PRICING_FORMULA_CONFIGURATOR_URL,
        "80%",
        "80%"
)

return ce