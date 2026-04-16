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
    select("SAPLineID", "SAPLineID")
    select("SoldTo", "SoldTo")
    select("ShipTo", "ShipTo")
    select("SoldtoName", "SoldtoName")
    select("ShiptoName", "ShiptoName")
    select("Material", "Material")
    select("RejectionFlag", "RejectionFlag")
    select("SalesPerson", "SalesPerson")
    select("ContractEffectiveDate", "ContractValidFrom")
    select("ContractExpiryDate", "ContractValidTo")
    select("ContractPricingDate", "CurrentContractPricingDate")
    select("PriceValidFrom", "NewContractPricingDate")
    select("QuoteLastUpdate", "QuoteLastUpdate")
    if (soldToValues && !doNotFilter) where(Filter.in("SoldTo", soldToValues))
    if (selectedShipTo && !selectedContract && !doNotFilter) where(Filter.in("ShipTo", selectedShipTo as List))
    if (selectedContract) where(Filter.in("SAPContractNumber", selectedContract?.collect { it.split("\\|").getAt(0).trim() }))
    where(Filter.isNotNull("SAPContractNumber"))
    orderBy("SAPContractNumber", "NewContractPricingDate ASC")
}

def rows = ctx.executeQuery(query).getData().toResultMatrix().getEntries() ?: []

def latestByContractLine = [:]

rows.each { r ->
    def k = r.SAPContractNumber + "|" + r.SAPLineID
    def current = latestByContractLine[k]

    if (!current) {
        latestByContractLine[k] = r
    } else {
        def currDate = current.QuoteLastUpdate
        def newDate  = r.QuoteLastUpdate
        if (newDate && (!currDate || newDate > currDate)) {
            latestByContractLine[k] = r
        }
    }
}

def tempRows = latestByContractLine.values() as List

def contractDetailsMap = [:].withDefault { [
        Materials     : [] as Set,
        SalesPersons  : [] as Set,
        RejectionFlags: [] as Set,
        Lines         : []
]}

tempRows.each { r ->
    def key = r.SAPContractNumber + "|" + r.SoldTo
    def bucket = contractDetailsMap[key]

    if (r.Material) bucket.Materials.add(r.Material)
    if (r.SalesPerson) bucket.SalesPersons.add(r.SalesPerson)
    bucket.RejectionFlags.add(r.RejectionFlag as boolean)

    bucket.Lines.add([
            Material     : r.Material,
            SalesPerson  : r.SalesPerson,
            RejectionFlag: r.RejectionFlag as boolean
    ])
}

return contractDetailsMap