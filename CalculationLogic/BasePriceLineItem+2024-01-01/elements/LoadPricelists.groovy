if(api.global.isFirstRow){
    def fields = ["name", "attribute1"]
    api.global.pricelists = api.findLookupTableValues("Pricelist", fields, "name")
                                        ?.collectEntries { [(it.name): it.attribute1] }
}

return api.global.pricelists