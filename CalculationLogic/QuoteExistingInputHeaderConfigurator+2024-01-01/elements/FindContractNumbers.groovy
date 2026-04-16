if (api.isInputGenerationExecution()) return

final tableConstants = libs.QuoteConstantsLibrary.Tables

def soldToValues = out.GetSoldToValues
def selectedShipTo = InputShipTo.input?.getValue()?.collect { it.split(" - ").getAt(0).trim() }
def selectedContract = InputContractNumber.input?.getValue()
def selectedMaterials = out.GetMaterialValues
def selectedLineRejected = InputLineRejected.input?.getValue()
def selectedSalesPerson = InputSalesPerson.input?.getValue()
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
    if (selectedMaterials) where(Filter.in("Material", selectedMaterials as List))
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

if (selectedLineRejected) {
    def rejectionBool = selectedLineRejected == "Y"
    tempRows = tempRows.findAll { row ->
        row.RejectionFlag == rejectionBool
    }
}

if (selectedSalesPerson) {
    tempRows = tempRows.findAll { row ->
        selectedSalesPerson == row.SalesPerson
    }
}

return tempRows.collectEntries { it ->
    [(it.SAPContractNumber + "|" + it.SoldTo): it]
}