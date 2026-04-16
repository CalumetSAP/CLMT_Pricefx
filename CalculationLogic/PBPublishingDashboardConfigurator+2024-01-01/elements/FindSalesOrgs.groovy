def fields = ["name", "attribute1"]

def result = api.findLookupTableValues("SalesOrg", fields, "name", Filter.equal("attribute4", true))
        ?.collectEntries { [(it.name): it.name + " - " + it.attribute1] }

return result