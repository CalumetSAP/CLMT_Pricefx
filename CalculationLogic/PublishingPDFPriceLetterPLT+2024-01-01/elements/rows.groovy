def sortedRows = api.local.sortedRows
def finalRows = sortedRows?.each { item ->
    def price = item["price"] ?: ""
    def priceUom = item["priceUOM"] ?: ""
    item["price"] = "${price} / ${priceUom}"
}

def grouped = finalRows?.groupBy { "${it.ph2Desc}" }

api.local.finalRows = grouped

return null