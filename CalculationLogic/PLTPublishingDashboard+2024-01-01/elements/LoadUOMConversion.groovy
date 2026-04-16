def materials = api.global.materials
def products = api.global.products.collectEntries{[(it.sku): [UOM: it.unitOfMeasure]]}?:[:]

api.global.uomConversion = libs.QuoteLibrary.Conversion.getUOMConversion(materials, products)

return null