import java.time.LocalDate

if (api.isInputGenerationExecution()) return

def calcItem = dist.calcItem
def dashboard = api.isDebugMode() ? "PB" : calcItem?.Value?.dashboard

if (dashboard != "PB") return

final dashboardQuery = libs.DashboardLibrary.Query

def quotes = out.LoadQuotes

List affectedVariants = dashboardQuery.findPendingAffectedVariantsByDashboard("PB") ?: []
List variantsData = dashboardQuery.findPBVariantsByNames(affectedVariants.Variant) ?: []
Map variantsDataMap = variantsData.collectEntries {
    [(it.key1): it]
} ?: [:]

Calendar cal = Calendar.getInstance()

LocalDate effectiveDateLD

def materialList = new HashSet()
def salesOrgList = new HashSet()
def soldToList = new HashSet()
def shipToList = new HashSet()
def pricelistList = new HashSet()
def salesPersonList = new HashSet()

def variant, filteredLines
def pricingDate, contracts, contractLines, materials, division, salesOrgs, soldTos, shipTos, pricelists, plant, salesPerson
for (affectedVariant in affectedVariants) {
    variant = variantsDataMap[affectedVariant.Variant]
    if (!variant) continue

    effectiveDateLD = affectedVariant.EffectiveDate as LocalDate
    cal.clear()
    cal.set(effectiveDateLD.year, effectiveDateLD.monthValue - 1, effectiveDateLD.dayOfMonth)
    pricingDate = cal.getTime()

    filteredLines = quotes

    contracts = variant.Contract
    contractLines = variant.ContractLine
    materials = []
    soldTos = variant.SoldTo
    shipTos = variant.ShipTo
    division = variant.Division
    salesOrgs = variant.SalesOrg
    pricelists = variant.Pricelist
    plant = variant.Plant
    salesPerson = variant.SalesPerson

    if (contracts) filteredLines = filteredLines.findAll { it.SAPContractNumber in contracts }
    if (contractLines) filteredLines = filteredLines.findAll { it.SAPLineID in contractLines }
    if (materials) filteredLines = filteredLines.findAll { it.Material in materials }
    if (soldTos) filteredLines = filteredLines.findAll { it.SoldTo in soldTos }
    if (shipTos) filteredLines = filteredLines.findAll { it.ShipTo in shipTos }
    if (division) filteredLines = filteredLines.findAll { it.Division == division }
    if (salesOrgs) filteredLines = filteredLines.findAll { it.SalesOrg in salesOrgs }
    if (pricelists) filteredLines = filteredLines.findAll { it.PriceListPLT in pricelists }
    if (plant) filteredLines = filteredLines.findAll { it.Plant in plant }
    if (salesPerson) filteredLines = filteredLines.findAll { it.SalesPerson in salesPerson }

    filteredLines = filteredLines.findAll { row -> row.PriceValidFrom && row.PriceValidTo }
    filteredLines = filteredLines.findAll { row ->
        row.PriceValidFrom <= pricingDate && row.PriceValidTo >= pricingDate
    }

    for (quote in filteredLines) {
        if (quote.Material) materialList.add(quote.Material)
        if (quote.SalesOrg) salesOrgList.add(quote.SalesOrg)
        if (quote.SoldTo) soldToList.add(quote.SoldTo)
        if (quote.ShipTo) shipToList.add(quote.ShipTo)
        if (quote.PriceListPLT) pricelistList.add(quote.PriceListPLT)
        if (quote.SalesPerson) salesPersonList.add(quote.SalesPerson)
    }
}

api.global.materialList = materialList?.toList()
api.global.salesOrgList = salesOrgList?.toList()
api.global.soldToList = soldToList?.toList()
api.global.shipToList = shipToList?.toList()
api.global.pricelistList = pricelistList?.toList()
api.global.salesPersonList = salesPersonList?.toList()

return null