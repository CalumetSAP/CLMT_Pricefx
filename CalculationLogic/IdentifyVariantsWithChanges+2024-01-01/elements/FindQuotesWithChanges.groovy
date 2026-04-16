if (api.isInputGenerationExecution()) return

def changesByContract = api.local.changesByContract as Map
def changesByPricelist = api.local.changesByPricelist as Map

def materials = api.local.materials as List
def contractNumbers = api.local.contractNumbers as List
def contractLines = api.local.contractLines as List
def pricelists = api.local.pricelists as List

def dmCtx = api.getDatamartContext()
def ds = dmCtx.getDataSource("Quotes")
def query = dmCtx.newQuery(ds, false)

def fields = ["Material", "SAPContractNumber", "SAPLineID", "PriceListPLT", "SalesOrg", "SoldTo", "ShipTo", "Division",
              "PriceValidFrom", "PriceValidTo", "RejectionFlag", "Plant", "SalesPerson", "QuoteLastUpdate"]

fields.each { query.select(it, it) }

query.where(Filter.notEqual("RejectionFlag", true))

if (materials) query.where(Filter.in("Material", materials))

def orFilters = []
if (contractNumbers && contractLines) {
    orFilters.add(Filter.and(
            Filter.in("SAPContractNumber", contractNumbers),
            Filter.in("SAPLineID", contractLines)
    ))
}
if (pricelists) {
    orFilters.add(Filter.in("PriceListPLT", pricelists))
}
if (orFilters) query.where(Filter.or(*orFilters))

query.orderBy("QuoteLastUpdate DESC")

def rows = dmCtx.executeQuery(query)?.getData() ?: []
def latestByContractLine = rows
        .groupBy { [it.SAPContractNumber, it.SAPLineID] }
        .collect { _, group -> group.max { it.QuoteLastUpdate } }

Map<String, Set<String>> idxByContract = [:].withDefault { new HashSet<String>() }
Map<String, Set<String>> idxByPricelist = [:].withDefault { new HashSet<String>() }

def dk, cn, cl, mat, pl, key, possibleEffDate
latestByContractLine.each { r ->
    dk = dimKey(r)
    cn = r.SAPContractNumber
    cl = r.SAPLineID
    mat = r.Material
    pl = r.PriceListPLT

    key = mat + "|" + cn + "|" + cl
    possibleEffDate = changesByContract[key]
    possibleEffDate?.each {
        idxByContract[key + "|" + it].add(dk)
    }

    key = mat + "|" + pl
    possibleEffDate = changesByPricelist[key]
    possibleEffDate?.each {
        idxByPricelist[key + "|" + it].add(dk)
    }
}

api.local.idxByContract = idxByContract
api.local.idxByPricelist = idxByPricelist

return null

String safe(v) { v == null ? "" : v.toString() }

String dimKey(row) {
    return safe(row.SAPContractNumber) + "|" + safe(row.SAPLineID) + "|" + safe(row.Material) + "|" +
            safe(row.SoldTo) + "|" +safe(row.ShipTo) + "|" +safe(row.Division) + "|" + safe(row.SalesOrg) + "|" +
            safe(row.PriceListPLT) + "|" +safe(row.Plant) + "|" +safe(row.SalesPerson)
}