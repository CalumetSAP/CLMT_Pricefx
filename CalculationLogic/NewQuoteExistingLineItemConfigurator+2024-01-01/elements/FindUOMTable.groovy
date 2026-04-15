if (api.isInputGenerationExecution()) return

def sku = InputMaterial?.input?.getValue()
def productsMap = [
        (sku): [
                "UOM": api.local.product?.UOM
        ]
]

api.local.uomTable = libs.QuoteLibrary.Conversion.getUOMConversion([sku], productsMap)

return null