def dmCtx = api.getDatamartContext()
def configurator = out.Filters

def getProductFromFilter(productGroupInput, List<String> ph1, List<String> ph2, List<String> ph3, List<String> ph4, List<String> brand) {

    def filter = null

    if (productGroupInput?.productFieldValue) {
        filter = Filter.equal(productGroupInput?.productFieldName, productGroupInput?.productFieldValue)
    } else if(productGroupInput?.productFilterCriteria){
        filter = api.filterFromMap(productGroupInput?.productFilterCriteria)
    }

    def otherFilters = []
    if(ph1) otherFilters.add(Filter.in("attribute14", ph1))
    if(ph2) otherFilters.add(Filter.in("attribute16", ph2))
    if(ph3) otherFilters.add(Filter.in("attribute18", ph3))
    if(ph4) otherFilters.add(Filter.in("attribute20", ph4))
    if(brand) otherFilters.add(Filter.in("attribute2", brand))

    def fields = ["sku", "label"]

    def products = api.stream("P", "sku", fields, true, *[filter, *otherFilters])?.withCloseable { it.collect{ it.sku } }

    return products
}

String pricingDate = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
List<String> salesOrg = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SALES_ORG_INPUT_KEY)
List<String> soldTo = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_KEY)
List<String> shipTo = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY)
String division = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.DIVISION_INPUT_KEY)
//String masterParent = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.MASTER_PARENT_INPUT_KEY)
List<String> ph1 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_1_INPUT_KEY)
List<String> ph2 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_2_INPUT_KEY)
List<String> ph3 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_3_INPUT_KEY)
List<String> ph4 = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_4_INPUT_KEY)

List<String> brand = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.BRAND_INPUT_KEY)
def products = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRODUCTS_INPUT_KEY)
List<String> contract = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_INPUT_KEY)//["TestContract1", "AOTest"]//["40022330", "40022318", "40027164"]
List<String> contractLine = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_LINE_INPUT_KEY)//["jl2", "2550"]//["10", "30"]
List<String> pricelists = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICELIST_INPUT_KEY)
List<String> plant = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PLANT_INPUT_KEY)
List<String> salesPerson = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.SALES_PERSON_INPUT_KEY)

def materials = null

if(products || ph1 || ph2 || ph3 || ph4 || brand) {
    materials = getProductFromFilter(products, ph1, ph2, ph3, ph4, brand)
}

def ds = dmCtx.getDataSource("Quotes")
def query = dmCtx.newQuery(ds, false)
query.select("QuoteID", "QuoteID")
query.select("LineID", "LineID")
query.select("SalesOrg", "SalesOrg")
query.select("SalesPerson", "SalesPerson")
query.select("Material", "Material")
query.select("MaterialDescription", "MaterialDescription")
query.select("CustomerMaterial", "CustomerMaterial")
query.select("ThirdPartyCustomer", "ThirdPartyCustomer")
query.select("Plant", "Plant")
query.select("ShipTo", "ShipTo")
query.select("SoldTo", "SoldTo")
query.select("SoldToName", "SoldToName")
query.select("ModeOfTransportation", "ModeOfTransportation")
query.select("MeansOfTransportation", "MeansOfTransportation")
query.select("EffectiveDate", "EffectiveDate")
query.select("MOQ", "MOQ")
query.select("MOQUOM", "MOQUOM")
query.select("Incoterm", "Incoterm")
query.select("FreightTermValue", "FreightTermValue")
query.select("FreightTerm", "FreightTerm")
query.select("FreightValidFrom", "FreightValidFrom")
query.select("FreightValidto", "FreightValidTo")
query.select("FreightAmount", "FreightAmount")
query.select("FreightUOM", "FreightUOM")
query.select("PriceType", "PriceType")
query.select("SAPContractNumber", "SAPContractNumber")
query.select("SAPLineID", "SAPLineID")
query.select("Per", "Per")
query.select("PriceListPLT", "PriceListPLT")
query.select("RejectionReason", "RejectionReason")
query.select("QuoteLastUpdate", "QuoteLastUpdate")
query.select("Price", "Price")
query.select("DeliveredPrice", "DeliveredPrice")
query.select("PricingUOM", "PricingUOM")
query.select("Per", "Per")
query.select("Currency", "Currency")
query.select("PriceValidFrom", "PriceValidFrom")
query.select("PriceValidTo", "PriceValidTo")
query.select("Division", "Division")
query.select("IndexNumberOne", "IndexNumberOne")
query.select("IndexNumberTwo", "IndexNumberTwo")
query.select("IndexNumberThree", "IndexNumberThree")
query.select("Adder", "Adder")
query.select("AdderUOM", "AdderUOM")
query.select("RecalculationDate", "RecalculationDate")
query.select("RecalculationPeriod", "RecalculationPeriod")
query.select("ReferencePeriodValue", "ReferencePeriodValue")
query.select("IndexIndicator", "IndexIndicator")
query.select("ShippingPoint", "ShippingPoint")
query.select("NamedPlace", "NamedPlace")
query.select("RejectionFlag", "RejectionFlag")

//query.where(Filter.isNotNull("Price"))
//query.where(Filter.isNotEmpty("Price"))

if (pricingDate) {
    query.where(
            Filter.and(
                    Filter.lessOrEqual("PriceValidFrom", pricingDate),
                    Filter.greaterOrEqual("PriceValidTo", pricingDate)
            )
    )
}

if(contract) query.where(Filter.in("SAPContractNumber", contract))
if(contractLine) query.where(Filter.in("SAPLineID", contractLine))
query.where(Filter.notEqual("RejectionFlag", true))

query.orderBy("QuoteLastUpdate DESC")

def result = null

if(contract || contractLine || materials || division || shipTo || soldTo || salesOrg || pricelists || plant || salesPerson) {
    def rows = dmCtx.executeQuery(query)?.getData() ?: []
    def latestByContractLine = rows
            .groupBy { [it.SAPContractNumber, it.SAPLineID] }
            .collect { _, group -> group.max { it.QuoteLastUpdate } }

    def postFiltered = latestByContractLine
    postFiltered = postFiltered.findAll { it.PriceType != "4" }
    postFiltered = postFiltered.findAll { it.SAPContractNumber }
    postFiltered = postFiltered.findAll { it.SAPLineID }
    if (salesOrg)     postFiltered = postFiltered.findAll { it.SalesOrg     in salesOrg }
    if (soldTo)       postFiltered = postFiltered.findAll { it.SoldTo       in soldTo }
    if (shipTo)       postFiltered = postFiltered.findAll { it.ShipTo       in shipTo }
    if (division)     postFiltered = postFiltered.findAll { it.Division     == division }
    if (materials)    postFiltered = postFiltered.findAll { it.Material     in materials }
    if (pricelists)   postFiltered = postFiltered.findAll { it.PriceListPLT in pricelists }
    if (plant)        postFiltered = postFiltered.findAll { it.Plant        in plant }
    if (salesPerson)  postFiltered = postFiltered.findAll { it.SalesPerson  in salesPerson }

    result = postFiltered.groupBy {
        if (it.PriceType == "3") {
            [ it.Material, it.CustomerMaterial, it.FreightTerm, it.Incoterm, normalizeBigDecimal(it.MOQ), it.MOQUOM ]
        } else {
            [ it.Material, it.CustomerMaterial, it.DeliveredPrice,
              it.PriceType, it.FreightTerm, it.Incoterm,
              normalizeBigDecimal(it.MOQ), it.MOQUOM ]
        }
    }
}

def allRows = result?.values()?.flatten()

api.local.allRows = allRows
api.local.quotes = result

def normalizeBigDecimal(BigDecimal x) {
    if (x == null) return null
    def n = x.stripTrailingZeros()

    return n.signum() == 0 ? BigDecimal.ZERO : n
}