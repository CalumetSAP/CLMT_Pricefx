def sortedRows = api.local.sortedRows
def finalRows = sortedRows?.each { item ->
    def price = item["price"] ?: ""
    def priceUom = item["priceUOM"] ?: ""
    item["price"] = "${price} / ${priceUom}"
}

return finalRows ?: []