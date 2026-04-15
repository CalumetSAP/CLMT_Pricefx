
def dmCtx = api.getDatamartContext()

def soldTo = out.SoldTo.getFirstInput().getValue()
def shipTo = out.ShipTo.getFirstInput().getValue()
def salesOrg = out.SalesOrg.getFirstInput().getValue()
def division = out.Division.getFirstInput().getValue()

def ds = dmCtx.getDataSource("Quotes")
def query = dmCtx.newQuery(ds)
query.select("SAPContractNumber", "SAPContractNumber")
query.select("SAPLineId", "SAPLineId")
query.select("PriceListPLT", "PriceListPLT")
query.select("Plant", "Plant")
query.select("SalesPerson", "SalesPerson")

if(soldTo) query.where(Filter.in("SoldTo", soldTo))
if(shipTo) query.where(Filter.in("ShipTo", shipTo))
if(salesOrg) query.where(Filter.in("SalesOrg", salesOrg))
if(division) query.where(Filter.equal("Division", division))

def result = dmCtx.executeQuery(query)?.getData()
api.local.quotes = result?.toResultMatrix()?.getEntries()