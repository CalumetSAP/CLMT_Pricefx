final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def ce
if (!api.global.itemsWithoutScales?.contains(api.local.lineId)) {
    ce = api.configurator(
            lineItemConstants.SCALES_CONFIGURATOR_NAME,
            lineItemConstants.SCALES_CONFIGURATOR_URL,
            "80%",
            "80%"
    )
}

return ce