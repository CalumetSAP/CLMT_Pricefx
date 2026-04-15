if (api.isInputGenerationExecution()) return

final tableConstants = libs.QuoteConstantsLibrary.Tables

def soldToValues = out.GetSoldToValues
def selectedShipTo = InputShipTo.input?.getValue()?.collect { it.split(" - ").getAt(0).trim() }
def selectedContract = InputContractNumber.input?.getValue()
def doNotFilter = out.InputDoNotFilterHidden?.getFirstInput()?.getValue()

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
    select("PriceValidFrom", "NewContractPricingDate")
    if (soldToValues && !doNotFilter) where(Filter.in("SoldTo", soldToValues))
    if (selectedShipTo && !selectedContract && !doNotFilter) where(Filter.in("ShipTo", selectedShipTo as List))
    where(Filter.isNotNull("SAPContractNumber"))
    orderBy("SAPContractNumber", "NewContractPricingDate ASC")
}

return ctx.executeQuery(query).getData().toResultMatrix().getEntries()?.collectEntries {
    [(it.SAPContractNumber + "|" + it.SoldTo): it]
} ?: [:]