import com.googlecode.genericdao.search.Filter

if (api.isInputGenerationExecution()) return

def uuid = api.isDebugMode() ? "UUID6" : input["uuid"]

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("AutomatePriceLetters")

def query = ctx.newQuery(dm, false)
        .select("Variant", "Variant")
        .select("ChangeDate", "ChangeDate")
        .select("Dashboard", "Dashboard")
        .select("EffectiveDateKey", "EffectiveDateKey")
        .select("SPSKey", "SPSKey")
        .select("PBKey", "PBKey")
        .select("effectiveDate", "effectiveDate")
        .select("salesOrg", "salesOrg")
        .select("material", "material")
        .select("modeOfTransportation", "modeOfTransportation")
        .select("meansOfTransportation", "meansOfTransportation")
        .select("salesRepName", "salesRepName")
        .select("materialLabel", "materialLabel")
        .select("materialAndLabel", "materialAndLabel")
        .select("customerMaterialNumber", "customerMaterialNumber")
        .select("origin", "origin")
        .select("deliveredLocation", "deliveredLocation")
        .select("modeOfSale", "modeOfSale")
        .select("wpg", "wpg")
        .select("freight", "freight")
        .select("index", "index")
        .select("indexNumberOne", "indexNumberOne")
        .select("indexNumberTwo", "indexNumberTwo")
        .select("indexNumberThree", "indexNumberThree")
        .select("adder", "adder")
        .select("adderUOM", "adderUOM")
        .select("recalculationDate", "recalculationDate")
        .select("recalculationPeriod", "recalculationPeriod")
        .select("referencePeriod", "referencePeriod")
        .select("currency", "currency")
        .select("buyingEntity", "buyingEntity")
        .select("ph1", "ph1")
        .select("ph2", "ph2")
        .select("ph3", "ph3")
        .select("moqUom", "moqUom")
        .select("priceUom", "priceUOM")
        .select("soldTo", "soldTo")
        .select("shipTo", "shipTo")
        .select("brand", "brand")
        .select("legacyPartNo", "legacyPartNo")
        .select("jobbers", "jobbers")
        .select("srp", "srp")
        .select("map", "map")
        .select("moq", "moq")
        .select("price", "price")
        .select("additionalNotes", "additionalNotes")
        .select("customer", "customer")
        .select("pricingDate", "pricingDate")
        .where(Filter.in("UUID", uuid))
        .where(Filter.equal("Dashboard", "PB"))

def result = ctx.executeQuery(query)?.getData()?.toList()
def firstRecord = result?.first()

api.local.rows = result

api.local.sortedRows = result?.sort (false){ a, b ->
    a.brand <=> b.brand ?: a.ph1 <=> b.ph1 ?: a.ph2 <=> b.ph2 ?: a.ph3 <=> b.ph3 ?: a.materialLabel <=> b.materialLabel ?: a.customerMaterialNumber <=> b.customerMaterialNumber ?: a.moq <=> b.moq
}

api.local.pricingDate = firstRecord?.pricingDate
api.local.hasCustomer = firstRecord?.customer ? true : false
api.local.customer = "Master Parent: " + firstRecord?.customer

def buyingEntities = result?.collect { it.buyingEntity }?.unique()?.join(", ")
api.local.salesOrg = result?.collect {it.salesOrg}
api.local.materials = result?.collect {it.material}

api.local.buyingEntities = "Buying Entities: " + buyingEntities
api.local.currency = firstRecord?.currency
api.local.dashboardFooter = api.jsonDecodeList(firstRecord?.additionalNotes as String)

def hasJobbers = result.find { it.jobbers }
def hasSRP = result.find { it.srp }
def hasMAP = result.find { it.map }

api.local.showAllColumns = hasJobbers && hasSRP && hasMAP
api.local.dontShowJobbers = !hasJobbers && hasSRP && hasMAP
api.local.dontShowSRP = hasJobbers && !hasSRP && hasMAP
api.local.dontShowMAP = hasJobbers && hasSRP && !hasMAP
api.local.showOnlyJobbers = hasJobbers && !hasSRP && !hasMAP
api.local.showOnlySRP = !hasJobbers && hasSRP && !hasMAP
api.local.showOnlyMAP = !hasJobbers && !hasSRP && hasMAP
api.local.showNothing = !hasJobbers && !hasSRP && !hasMAP