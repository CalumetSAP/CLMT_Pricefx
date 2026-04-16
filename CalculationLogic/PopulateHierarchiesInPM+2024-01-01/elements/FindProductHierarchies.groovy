if (api.isInputGenerationExecution()) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def fields = ["sku", "attribute2"]
def filters = [
        Filter.equal("name", tablesConstants.PRODUCT_EXTENSION_PRODUCT_HIERARCHY)
]

def result = api.stream("PX", null, fields, *filters)?.withCloseable { it.collectEntries {
    [(it.sku): it.attribute2]
}}

api.global.productHierarchies = result

return null