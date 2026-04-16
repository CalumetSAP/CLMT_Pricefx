api.local.rows = input["rows"]

api.local.sortedRows = input["rows"]?.sort (false){ a, b ->
    a.brand <=> b.brand ?: a.ph1 <=> b.ph1 ?: a.ph2 <=> b.ph2 ?: a.ph3 <=> b.ph3 ?: a.materialLabel <=> b.materialLabel ?: a.customerMaterialNumber <=> b.customerMaterialNumber ?: a.moq <=> b.moq
}

api.local.pricingDate = input["pricingDate"]
api.local.hasJobbers = input["hasJobbers"]
api.local.hasSRP = input["hasSRP"]
api.local.hasMAP = input["hasMAP"]

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

api.local.currency = firstRow?.currency

api.local.additionalNotes = input["additionalNotes"]

return null