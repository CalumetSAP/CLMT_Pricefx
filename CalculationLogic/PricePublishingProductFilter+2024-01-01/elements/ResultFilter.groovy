if (api.isInputGenerationExecution()) return

def data = api.jsonDecode(filterFormulaParam)

def filters = []

if (data.PH1) filters.add(Filter.in("attribute14", data.PH1 as List))
if (data.PH2) filters.add(Filter.in("attribute16", data.PH2 as List))
if (data.PH3) filters.add(Filter.in("attribute18", data.PH3 as List))
if (data.PH4) filters.add(Filter.in("attribute20", data.PH4 as List))
if (data.Brand) filters.add(Filter.in("attribute2", data.Brand as List))

return filters ? Filter.and(*filters) : []