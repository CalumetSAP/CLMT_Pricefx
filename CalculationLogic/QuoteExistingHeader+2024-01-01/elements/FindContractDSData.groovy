if (!quoteProcessor.isPrePhase() || api.isInputGenerationExecution()) return

final tableConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedContracts = headerConfigurator?.get(headerConstants.CONTRACT_NUMBER_ID) ?: []

if (!api.local.addedContracts && !selectedContracts) return

def materials = headerConfigurator?.get(headerConstants.MATERIAL_VALUES_HIDDEN_ID) ?: []
def lineRejected = headerConfigurator?.get(headerConstants.LINE_REJECTED_ID)
def salesPerson = headerConfigurator?.get(headerConstants.SALES_PERSON_ID) ?: []

def ctx = api.getDatamartContext()
def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
def query = ctx.newQuery(dataSource, false)

query.identity {
    select("SAPContractNumber", "SAPContract")
    select("SAPLineID", "LineNumber")
    select("QuoteID", "QuoteID")
    select("LineID", "LineID")
    select("Material", "Material")
    select("MaterialDescription", "Description")
    select("SoldTo", "SoldTo")
    select("ShipTo", "ShipTo")
    select("ShiptoName", "ShipToName")
    select("Plant", "Plant")
    select("Incoterm", "Incoterm")
    select("FreightTerm", "FreightTerm")
    select("NamedPlace", "NamedPlace")
    select("ShippingPoint", "ShippingPoint")
    select("PricingUOM", "PricingUOM")
    select("Price", "Price")
    select("DeliveredPrice", "DeliveredPrice")
    select("Currency", "Currency")
    select("NumberofDecimals", "NumberofDecimals")
    select("CompetitorPrice", "CompetitorPrice")
    select("MOQ", "MOQ")
    select("ContractPricingDate", "CurrentContractPricingDate")
    select("PriceValidFrom", "PriceValidFrom")
    select("PriceValidTo", "PriceValidTo")
    select("SalesPerson", "SalesPerson")
    select("CustomerMaterial", "CustomerMaterial")
    select("ThirdPartyCustomer", "ThirdPartyCustomer")
    select("MeansOfTransportation", "MeansOfTransportation")
    select("ModeOfTransportation", "ModeOfTransportation")
    select("PriceListPLT", "PriceListPLT")
    select("ApprovalSequence", "ApprovalSequence")
    select("DiscountApprover", "DiscountApprover")
    select("Cost", "Cost")
    select("GuardrailPrice", "RecommendedPrice")
    select("MaterialPackageStyle", "MaterialPackageStyle")
    select("MOQUOM", "MOQUOM")
    select("Per", "Per")
    select("PriceType", "PriceType")
    select("IndexNumberOne", "IndexNumberOne")
    select("IndexNumberTwo", "IndexNumberTwo")
    select("IndexNumberThree", "IndexNumberThree")
    select("IndexNumberOnePercent", "IndexNumberOnePercent")
    select("IndexNumberTwoPercent", "IndexNumberTwoPercent")
    select("IndexNumberThreePercent", "IndexNumberThreePercent")
    select("ReferencePeriod", "ReferencePeriod")
    select("Adder", "Adder")
    select("AdderUOM", "AdderUOM")
    select("RecalculationDate", "RecalculationDate")
    select("RecalculationPeriod", "RecalculationPeriod")
    select("ReferencePeriodValue", "ReferencePeriodValue")
    select("FormulaApprover", "FormulaApprover")
    select("IndexIndicator", "IndexIndicator")
    select("lastUpdateDate", "lastUpdateDate")
    select("Division", "Division")
    select("SalesOrg", "SalesOrg")
    select("FreightEstimate", "FreightEstimate")
    select("FreightAmount", "FreightAmount")
    select("FreightUOM", "FreightUOM")
    select("FreightValidto", "FreightValidto")
    select("FreightValidFrom", "FreightValidFrom")
    select("RejectionFlag", "RejectionFlag")
    select("RejectionReason", "RejectionReason")
    select("QuoteLastUpdate", "QuoteLastUpdate")

    if (materials) where(Filter.in("Material", materials))
    where(Filter.in("SAPContractNumber", selectedContracts?.collect { it.split("\\|").getAt(0).trim() }))
    orderBy("QuoteLastUpdate DESC")
    selectDistinct()
}

def resultIterator = ctx.streamQuery(query)
def map = [:]
def addedRows = []
def r= 0
def division
while (resultIterator.next()) {
    def row = resultIterator.get()  // current row as map
    if (!map.containsKey(row.SAPContract)) map[row.SAPContract] = []
    if (!addedRows.contains(row.SAPContract + row.LineNumber)) {
        map[row.SAPContract].add(row)
        addedRows.add(row.SAPContract + row.LineNumber)
    }
    if (!division) division = row.Division
    r++
}

resultIterator.close()

if (salesPerson) {
    map = map.collectEntries { contract, lines ->
        def filtered = lines.findAll { row -> salesPerson.contains(row.SalesPerson) }
        filtered ? [(contract): filtered] : [:]
    }
}

if (lineRejected) {
    map = map.collectEntries { contract, lines ->
        def filtered
        if (lineRejected == "Y") filtered = lines.findAll { row -> row.RejectionFlag }
        if (lineRejected == "N") filtered = lines.findAll { row -> !row.RejectionFlag }

        filtered ? [(contract): filtered] : [:]
    }
}

api.local.contractLineSet = map.collectMany { contract, lines ->
    lines.collect { contract + "|" + it.LineNumber }
}.toSet()

api.local.division = division

return map