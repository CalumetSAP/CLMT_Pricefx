if (api.isInputGenerationExecution()) return [:]

final tableConstants = libs.QuoteConstantsLibrary.Tables

def ctx = api.getDatamartContext()
def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
def query = ctx.newQuery(dataSource, false)

def filters = []
filters.add(Filter.equal("SAPContractNumber", InputSAPContractNumber?.input?.getValue()))
filters.add(Filter.equal("SAPLineID", InputSAPLineId?.input?.getValue()))
filters.add(Filter.equal("Material", InputMaterial?.input?.getValue()))
filters.add(Filter.lessOrEqual("FreightValidFrom", InputPriceValidFrom?.input?.getValue()))
filters.add(Filter.greaterOrEqual("FreightValidto", InputPriceValidFrom?.input?.getValue()))

query.identity {
    select("FreightAmount", "FreightAmount")
    select("FreightValidFrom", "FreightValidFrom")
    select("FreightValidto", "FreightValidTo")
    select("FreightUOM", "FreightUOM")
    select("QuoteLastUpdate", "QuoteLastUpdate")

    where(Filter.and(*filters))
    orderBy("QuoteLastUpdate DESC")
    selectDistinct()
}

def result = ctx.executeQuery(query)
def data = result?.getData()?.find()

return data