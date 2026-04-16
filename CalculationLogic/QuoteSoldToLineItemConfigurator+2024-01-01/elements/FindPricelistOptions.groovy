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
def data = result?.getData()?.collectEntries { [(it.PricelistID): pricelists?.get(it.PricelistID)] }

return data ?: [:]
