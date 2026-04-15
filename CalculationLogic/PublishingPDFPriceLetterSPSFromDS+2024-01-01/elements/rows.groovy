def removeNulls = api.local.sortedRows?.each {
    it.replaceAll { k, v -> v == null || removeSlash(v) ? "" : v }
}

return removeNulls ?: []

def removeSlash(value) {
    if (value instanceof String) {
        return value?.trim() == "/"
    }
    return false
}