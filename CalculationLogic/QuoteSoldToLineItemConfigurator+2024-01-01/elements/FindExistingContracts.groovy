if (api.isInputGenerationExecution()) return

final tableConstants = libs.QuoteConstantsLibrary.Tables
final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def shipTos = InputShipToMatrix?.input?.getValue()?.collect { it[soldToConstants.MATRIX_SHIP_TO_ID]?.toString()?.split(" - ")?.getAt(0) }
if (!shipTos) return

def division = api.local.contractData?.Division
def salesOrg = api.local.contractData?.SalesOrg
def soldTo = api.local.contractData?.SoldTo

def ctx = api.getDatamartContext()
def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
def query = ctx.newQuery(dataSource, false)

query.identity {
    select("SAPContractNumber", "SAPContractNumber")
    select("SoldTo", "SoldTo")
    select("ShipTo", "ShipTo")
    select("Division", "Division")
    select("SalesOrg", "SalesOrg")
    select("lastUpdateDate", "lastUpdateDate")

    where(Filter.isNotNull("SAPContractNumber"))
    where(Filter.isNotNull("SoldTo"))
    where(Filter.isNotNull("ShipTo"))
    where(Filter.isNotNull("Division"))
    where(Filter.isNotNull("SalesOrg"))
    where(Filter.isNotEmpty("SAPContractNumber"))
    where(Filter.isNotEmpty("SoldTo"))
    where(Filter.isNotEmpty("ShipTo"))
    where(Filter.isNotEmpty("Division"))
    where(Filter.isNotEmpty("SalesOrg"))
    where(Filter.equal("Division", division))
    where(Filter.equal("SalesOrg", salesOrg))
    where(Filter.equal("SoldTo", soldTo))
    where(Filter.in("ShipTo", shipTos as List))

    orderBy("lastUpdateDate DESC")
    selectDistinct()
}

def resultIterator = ctx.streamQuery(query)
def existingContracts = [:]

while (resultIterator.next()) {
    def row = resultIterator.get()
    existingContracts.putIfAbsent(row.ShipTo, row.SAPContractNumber)
}

resultIterator.close()

return existingContracts