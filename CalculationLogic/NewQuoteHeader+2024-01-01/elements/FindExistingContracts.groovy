import java.text.SimpleDateFormat

if (!quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final tableConstants = libs.QuoteConstantsLibrary.Tables
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def sdf = new SimpleDateFormat("yyyy-MM-dd")

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def soldTo = headerConfigurator?.get(headerConstants.SOLD_TO_ID)

def today = sdf.parse(sdf.format(new Date()))

def ctx = api.getDatamartContext()
def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
def query = ctx.newQuery(dataSource, false)

query.identity {
    select("SAPContractNumber", "SAPContractNumber")
    select("SAPLineID", "SAPLineID")
    select("SoldTo", "SoldTo")
    select("ShipTo", "ShipTo")
    select("Division", "Division")
    select("SalesOrg", "SalesOrg")
    select("Material", "Material")
    select("ThirdPartyCustomer", "ThirdPartyCustomer")
    select("Plant", "Plant")
    select("MeansOfTransportation", "MeansOfTransportation")
    select("ModeOfTransportation", "ModeOfTransportation")
    select("Incoterm", "Incoterm")
    select("QuoteLastUpdate", "QuoteLastUpdate")
    select("RejectionFlag", "RejectionFlag")
    select("ContractExpiryDate", "ContractExpiryDate")

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
    where(Filter.in("ShipTo", api.local.lineItemShipTos as List))
    where(Filter.notEqual("RejectionFlag", true))

    orderBy("QuoteLastUpdate DESC")
    selectDistinct()
}

def resultIterator = ctx.streamQuery(query)
def latestLines = [:]

while (resultIterator.next()) {
    def row = resultIterator.get()
    def key = row.SAPContractNumber + "|" + row.SAPLineID
    latestLines.putIfAbsent(key, row)
}

def filteredLines = latestLines?.values()?.findAll { row ->
    if (!row.ContractExpiryDate) return false
    return sdf.parse(row.ContractExpiryDate?.toString()) > today
}

def existingContracts = [:]
def duplicatedLines = [:]

filteredLines.each { row ->
    existingContracts.putIfAbsent(row.ShipTo, row.SAPContractNumber)
    def key = row.ShipTo + "|" + (row.Material ?: "") + "|" + (row.ThirdPartyCustomer ?: "") + "|" + (row.Plant ?: "") + "|" + (row.MeansOfTransportation ?: "") +
            "|" + (row.ModeOfTransportation ?: "") + "|" + (row.Incoterm ?: "")
    duplicatedLines.putIfAbsent(key, row)
}

resultIterator.close()

api.local.duplicatedLines = duplicatedLines

return existingContracts