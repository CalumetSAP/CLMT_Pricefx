if (api.isInputGenerationExecution()) return

def materials = api.global.products?.collect { it.sku } ?: []
def products = api.global.products.collectEntries{[(it.sku): [UOM: it.unitOfMeasure]]} ?: [:]

api.global.uomConversion = libs.QuoteLibrary.Conversion.getUOMConversion(materials, products)

return null