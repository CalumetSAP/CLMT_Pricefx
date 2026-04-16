if (!api.global.plantsDescriptions) {
    api.global.plantsDescriptions = api.findLookupTableValues("Plant", ["name", "attribute1"], null).collectEntries { [(it.name): it.attribute1] }
}

return api.global.plantsDescriptions?.get(api.local.plantCode)