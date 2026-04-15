if (api.isInputGenerationExecution() || !InputPricelist?.input?.getValue() || (InputPriceType.input?.getValue() != "3" && InputPriceType.input?.getValue() != "2")) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_PRICING)

def sku = InputMaterial?.input?.getValue()
def effectiveDate = InputPriceValidFrom?.input?.getValue()
def expiryDate = InputPriceValidTo?.input?.getValue()
def pricelist = InputPricelist?.input?.getValue()?.split(" - ")?.getAt(0)
def priceType = InputPriceType.input?.getValue()

def customFilter = Filter.and(
//        Filter.lessOrEqual("ValidFrom", effectiveDate),
//        Filter.greaterOrEqual("ValidTo", effectiveDate),
        Filter.equal("Material", sku),
        Filter.equal("Pricelist", pricelist),
        Filter.equal("SalesOrganization", api.local.salesOrg)
)

def query = ctx.newQuery(dm, false)
        .select("Material", "ProductId")
        .select("ValidFrom", "ValidFrom")
        .select("ValidTo", "ValidTo")
        .select("SalesOrganization", "SalesOrganization")
        .select("ConditionRecordNo", "ConditionRecordNo")
        .select("Pricelist", "PricelistID")
        .select("Amount", "BasePrice")
        .select("Per", "Per")
        .select("UnitOfMeasure", "UOM")
        .select("ScaleUoM", "ScaleUOM")
        .select("lastUpdateDate", "lastUpdateDate")
        .where(customFilter)
        .orderBy("lastUpdateDate DESC")

def result = ctx.executeQuery(query)
def data = result?.getData()?.toList()

if (data?.UOM && shouldDefaultUOM(priceType)) InputPricingUOM?.input?.setValue(data?.find()?.UOM)

return data

def shouldDefaultUOM(priceType) {
    return priceType == "3" //|| (priceType == "2" && api.local.pricelistHasChanged)
}