def sortedRows = api.local.sortedRows
def finalRows = sortedRows?.each { item ->
    def price = item["price"] ?: ""
    def priceUom = item["priceUOM"] ?: ""
    item["price"] = "${price} / ${priceUom}"
}

def removeNulls = finalRows?.each {
    it.replaceAll { k, v -> v == null || removeSlash(v) ? "" : v }
}

return removeNulls ?: []

def removeSlash(value) {
    if (value instanceof String) {
        return value?.trim() == "/"
    }
    return false
}