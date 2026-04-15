def indexedItems = []

if(api.local.rows?.findAll { it.index }?.size() > 0) indexedItems << ""

return indexedItems