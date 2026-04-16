def materials = api.local.allRows?.collect { it.Material }?.unique() ?: []
def products = api.global.products.collectEntries{[(it.sku): [UOM: it.unitOfMeasure]]}?:[:]

api.global.uomConversion = libs.QuoteLibrary.Conversion.getUOMConversion(materials, products)

return api.global.uomConversion