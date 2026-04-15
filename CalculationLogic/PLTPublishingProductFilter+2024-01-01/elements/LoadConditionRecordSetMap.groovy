api.local.conditionRecordSetMap = api.find("CRCS", 0, api.getMaxFindResultsLimit(), null, ["uniqueName", "id"])?.collectEntries {
    [(it.uniqueName): it.id]
} ?: [:]


return null