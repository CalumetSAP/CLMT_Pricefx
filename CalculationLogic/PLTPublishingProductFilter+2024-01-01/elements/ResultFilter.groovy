if (api.isInputGenerationExecution()) return

def data = api.jsonDecode(filterFormulaParam)
def brand = data.Brand ?: []

def filters = []

def materials = (api.local.zbplMaterial + api.local.zbplCRMaterial).unique() as List
if (materials) filters.add(Filter.in("sku", materials))
if (brand) filters.add(Filter.in("attribute2", brand))

return filters ? Filter.and(*filters) : null