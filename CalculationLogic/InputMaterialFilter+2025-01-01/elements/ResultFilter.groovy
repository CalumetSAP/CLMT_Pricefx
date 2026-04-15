if (api.isInputGenerationExecution()) return

def data = api.jsonDecode(filterFormulaParam)
if (data.MaterialList) return Filter.in("sku", data.MaterialList)

return Filter.isNotNull("sku")
