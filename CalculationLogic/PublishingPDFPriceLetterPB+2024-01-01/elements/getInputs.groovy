api.local.rows = input["rows"]

api.local.sortedRows = input["rows"]?.sort (false){ a, b ->
    a.brand <=> b.brand ?: a.ph1 <=> b.ph1 ?: a.ph2 <=> b.ph2 ?: a.ph3 <=> b.ph3 ?: a.materialLabel <=> b.materialLabel ?: a.customerMaterialNumber <=> b.customerMaterialNumber ?: a.moq <=> b.moq
}

api.local.pricingDate = input["pricingDate"]
api.local.showAdder = input["showAdder"]
api.local.hasCustomer = input["customer"] ? true : false
api.local.customer = "Master Parent: " + input["customer"]

def firstRow = api.local.rows?.find{it}
def buyingEntities = api.local.rows?.collect{it.buyingEntity}?.unique()?.join(", ")
api.local.salesOrg = api.local.rows?.collect{it.salesOrg}
api.local.materials = api.local.rows?.collect{it.material}

api.local.buyingEntities = "Buying Entities: " + buyingEntities
api.local.currency = firstRow?.currency
api.local.dashboardFooter = input["additionalNotes"]

def hasJobbers = input["hasJobbers"]
def hasSRP = input["hasSRP"]
def hasMAP = input["hasMAP"]

api.local.showAllColumns = hasJobbers && hasSRP && hasMAP
api.local.dontShowJobbers = !hasJobbers && hasSRP && hasMAP
api.local.dontShowSRP = hasJobbers && !hasSRP && hasMAP
api.local.dontShowMAP = hasJobbers && hasSRP && !hasMAP
api.local.showOnlyJobbers = hasJobbers && !hasSRP && !hasMAP
api.local.showOnlySRP = !hasJobbers && hasSRP && !hasMAP
api.local.showOnlyMAP = !hasJobbers && !hasSRP && hasMAP
api.local.showNothing = !hasJobbers && !hasSRP && !hasMAP