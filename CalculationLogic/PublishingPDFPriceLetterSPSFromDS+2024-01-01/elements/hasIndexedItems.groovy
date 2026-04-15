def indexedItems = []

if(api.local.rows?.findAll { it.index && it.index != "X" }?.size() > 0) indexedItems << ""

return indexedItems