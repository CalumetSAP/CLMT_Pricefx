if (api.isInputGenerationExecution()) return

def dmCtx = api.getDatamartContext()
def ds = dmCtx.getDataSource("Quotes")
def query = dmCtx.newQuery(ds, false)

query.identity {
    select("QuoteID", "QuoteID")
    select("LineID", "LineID")
    select("SalesOrg", "SalesOrg")
    select("SalesPerson", "SalesPerson")
    select("Material", "Material")
    select("MaterialDescription", "MaterialDescription")
    select("CustomerMaterial", "CustomerMaterial")
    select("ThirdPartyCustomer", "ThirdPartyCustomer")
    select("Plant", "Plant")
    select("ShipTo", "ShipTo")
    select("SoldTo", "SoldTo")
    select("SoldToName", "SoldToName")
    select("ModeOfTransportation", "ModeOfTransportation")
    select("MeansOfTransportation", "MeansOfTransportation")
    select("EffectiveDate", "EffectiveDate")
    select("MOQ", "MOQ")
    select("MOQUOM", "MOQUOM")
    select("Incoterm", "Incoterm")
    select("FreightTermValue", "FreightTermValue")
    select("FreightTerm", "FreightTerm")
    select("FreightValidFrom", "FreightValidFrom")
    select("FreightValidto", "FreightValidTo")
    select("FreightAmount", "FreightAmount")
    select("FreightUOM", "FreightUOM")
    select("PriceType", "PriceType")
    select("SAPContractNumber", "SAPContractNumber")
    select("SAPLineID", "SAPLineID")
    select("Per", "Per")
    select("PriceListPLT", "PriceListPLT")
    select("RejectionReason", "RejectionReason")
    select("QuoteLastUpdate", "QuoteLastUpdate")
    select("Price", "Price")
    select("DeliveredPrice", "DeliveredPrice")
    select("PricingUOM", "PricingUOM")
    select("Currency", "Currency")
    select("PriceValidFrom", "PriceValidFrom")
    select("PriceValidTo", "PriceValidTo")
    select("Division", "Division")
    select("IndexNumberOne", "IndexNumberOne")
    select("IndexNumberTwo", "IndexNumberTwo")
    select("IndexNumberThree", "IndexNumberThree")
    select("Adder", "Adder")
    select("AdderUOM", "AdderUOM")
    select("RecalculationDate", "RecalculationDate")
    select("RecalculationPeriod", "RecalculationPeriod")
    select("ReferencePeriodValue", "ReferencePeriodValue")
    select("IndexIndicator", "IndexIndicator")
    select("ShippingPoint", "ShippingPoint")
    select("NamedPlace", "NamedPlace")
    select("RejectionFlag", "RejectionFlag")

    where(Filter.isNotNull("SAPContractNumber"))
    where(Filter.isNotNull("SAPLineID"))
    where(Filter.notEqual("RejectionFlag", true))

    orderBy("QuoteLastUpdate DESC")
}


def rows = dmCtx.executeQuery(query).getData().toResultMatrix().getEntries() ?: []

return rows
        .groupBy { [it.SAPContractNumber, it.SAPLineID] }
        .collect { _, group -> group.max { it.QuoteLastUpdate } }