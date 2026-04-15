api.local.rows = input["rows"]?.sort (false){ a, b ->
    a.index  <=> b.index
}

api.local.sortedRows = input["rows"]?.sort (false){ a, b ->
    a.ph1 <=> b.ph1 ?: a.ph2 <=> b.ph2 ?: a.materialLabel <=> b.materialLabel ?: a.origin <=> b.origin
}

api.local.pricingDate = input["pricingDate"]
api.local.showAdder = input["showAdder"]
api.local.hasCustomer = input["customer"] ? true : false
api.local.customer = "Master Parent: " + input["customer"]

def firstRow = api.local.rows?.find{it}
def buyingEntities = api.local.rows?.collect{it.buyingEntity}?.unique()?.join(", ")
api.local.salesOrg = api.local.rows?.collect{it.salesOrg}
api.local.materials = api.local.rows?.collect{it.material}
api.local.hasBulkRailItem = api.local.rows.find{it.modeOfTransportation == "R0" && it.meansOfTransportation == "RR"}

api.local.buyingEntities = "Buying Entities: " + buyingEntities
api.local.currency = firstRow?.currency
api.local.dashboardFooter = input["additionalNotes"]