api.local.rows = input["rows"]?.sort (false){ a, b ->
    a.index  <=> b.index
}

api.local.sortedRows = input["rows"]?.sort (false){ a, b ->
    a.ph1 <=> b.ph1 ?: a.ph2 <=> b.ph2 ?: a.materialLabel <=> b.materialLabel ?: a.origin <=> b.origin
}

api.local.pricingDate = input["pricingDate"]
api.local.showAdder = input["showAdder"] ? true : false

def customerAndBuyingEntities = []

def hasCustomer = input["customer"] ? true : false
customerAndBuyingEntities.add(
        [
                Title: hasCustomer ? "Master Parent:" : null,
                Space: null,
                Value: hasCustomer ? input["customer"] : null
        ]
)

def buyingEntities = api.local.rows?.collect{it.buyingEntity}?.unique()?.join(", ")
customerAndBuyingEntities.add(
        [
                Title: "Buying Entities:",
                Space: null,
                Value: buyingEntities
        ]
)
api.local.customerAndBuyingEntities = customerAndBuyingEntities

def firstRow = api.local.rows?.find{it}
api.local.salesOrg = api.local.rows?.collect{it.salesOrg}
api.local.materials = api.local.rows?.collect{it.material}
api.local.hasBulkRailItem = api.local.rows.find{it.modeOfTransportation == "R0" && it.meansOfTransportation == "RR"} ? true : false

api.local.currency = firstRow?.currency

api.local.additionalNotes = input["additionalNotes"]

return null