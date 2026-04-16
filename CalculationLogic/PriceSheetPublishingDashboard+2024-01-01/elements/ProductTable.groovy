def items = out.LoadPricing

def columnLabels = [
        "Item"          : "Item #",
        "SAP_Item"      : "SAP Item #",
        "Description"   : "Description",
        "MOQ"           : "MOQ",
        "MOQ_UOM"       : "MOQ UOM",
        "Price"         : "Price",
        "Price_UOM"     : "Price UOM",
        "Channel"       : "Channel",
]

def matrix = api.newMatrix(columnLabels.collect({ column -> column.value }))

items?.each{ item ->
    def row = [:]

    row[columnLabels.Item]          = item.ItemNumber
    row[columnLabels.SAP_Item]      = item.ProductId
    row[columnLabels.Description]   = item.Description
    row[columnLabels.MOQ]           = ""//TODO: add MOQ
    row[columnLabels.MOQ_UOM]       = ""//TODO: add MOQ UOM
    row[columnLabels.Price]         = item.BasePrice
    row[columnLabels.Price_UOM]     = item.UOM
    row[columnLabels.Channel]       = item.Channel

    matrix.addRow(row)
}

matrix.setEnableClientFilter(true)
matrix.setTitle("Items")

return matrix
