def data = out.LoadPricing
api.logInfo("FERCAMM data", out.LoadPricing)

def columnLabels = [
        "Item"              : "Item #",
        "SAPItem"           : "SAP Item #",
        "Description"       : "Description",
        "UOM"               : "UOM",
        "Notes"             : "Notes",
        "DistributorMIN"    : "Distributor <219 gal",
        "DistributorMED"    : "Distributor >220 gal",
        "DistributorMAX"    : "Distributor >880",
        "WholesaleMIN"      : "Wholesale <219 gal",
        "WholesaleMED"      : "Wholesale >220 gal",
        "WholesaleMAX"      : "Wholesale >880",
]
def matrix = api.newMatrix(columnLabels.collect({ column -> column.value }))

data.each{
    def row = [
            columnLabels.Item               = it.ItemNumber,
            columnLabels.SAPItem            = it.ProductId,
            columnLabels.Description        = it.Description,
            columnLabels.UOM                = it.UOM,
            columnLabels.Notes              = "",
            columnLabels.DistributorMIN     = it.BasePrice_01,
            columnLabels.DistributorMED     = it.BasePrice_01,
            columnLabels.DistributorMAX     = it.BasePrice_01,
            columnLabels.WholesaleMIN       = it.BasePrice_02,
            columnLabels.WholesaleMED       = it.BasePrice_02,
            columnLabels.WholesaleMAX       = it.BasePrice_02,
    ]
    matrix.addRow(row)
}

return matrix