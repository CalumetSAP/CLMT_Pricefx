if (api.isInputGenerationExecution() || !(InputPriceType?.input?.getValue() == "2" || InputPriceType?.input?.getValue() == "3")) return [:]

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_PRICING)

def sku = InputMaterial?.input?.getValue()
def effectiveDate = InputPriceValidFrom?.input?.getValue()
def expiryDate = InputPriceValidTo?.input?.getValue()

def customFilter = Filter.and(
        Filter.lessOrEqual("ValidFrom", effectiveDate),
        Filter.greaterOrEqual("ValidTo", effectiveDate),
        Filter.equal("Material", sku),
        Filter.isNotNull("Amount")
)

def query = ctx.newQuery(dm, false)
        .select("Pricelist", "PricelistID")
        .where(customFilter)

def result = ctx.executeQuery(query)
def pricelists = api.local.pricelists ?: [:]
def data = result?.getData()?.collect { it.PricelistID } ?: []

List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
               "conditionValue", "currency", "attribute2", "attribute3", "lastUpdateDate"]

List filters = [
        Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL"),
        Filter.lessOrEqual("validFrom", effectiveDate),
        Filter.greaterOrEqual("validTo", effectiveDate),
        Filter.or(
                Filter.equal("attribute4", "Change"),
                Filter.isNull("attribute4")
        ),
        Filter.equal("key5", sku),
        Filter.equal("key2", api.local.salesOrg),
        Filter.isNull("attribute5")
]

def crData = api.stream("CRCI5", "-lastUpdateDate", fields, *filters)?.withCloseable {
    it.collect { it.key4 }
} ?: []

data.addAll(crData)
data.unique()

return data?.collectEntries { [(it): pricelists?.get(it)] }?.sort()