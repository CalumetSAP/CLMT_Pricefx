if (api.isInputGenerationExecution() || quoteProcessor?.isPostPhase()) return

final tableConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def soldToValues = []

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedContracts = headerConfigurator?.get(headerConstants.CONTRACT_NUMBER_ID) ?: []

def ctx = api.getDatamartContext()
def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
def query = ctx.newQuery(dataSource, false)

query.identity {
    select("SAPContractNumber", "SAPContractNumber")
    select("SoldTo", "SoldTo")
    select("ShipTo", "ShipTo")
    select("SoldtoName", "SoldtoName")
    select("ShiptoName", "ShiptoName")
    select("ContractPO", "ContractPO")
    select("ContractEffectiveDate", "ContractValidFrom")
    select("ContractExpiryDate", "ContractValidTo")
    select("ContractPricingDate", "CurrentContractPricingDate")
    select("PriceValidFrom", "PriceValidFrom")
    where(Filter.in("SAPContractNumber", selectedContracts?.collect { it.split("\\|").getAt(0).trim()} as List))
    orderBy("lastUpdateDate DESC", "PriceValidFrom DESC")
}

// api.trace("selectedContracts", null, selectedContracts)

def result = ctx.executeQuery(query).getData().toResultMatrix().getEntries()

api.local.contractsDSData = result ?: [:]

def key
def map = [:]
result?.each {
    key = it.SAPContractNumber + "|" + it.SoldTo
    if (map.containsKey(key)) return
    map.put(key, it)
}

return map