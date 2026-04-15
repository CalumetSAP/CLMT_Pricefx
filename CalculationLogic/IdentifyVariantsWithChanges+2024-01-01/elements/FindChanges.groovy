if (api.isInputGenerationExecution()) return

def hoursBefore = 72
Calendar calendar = Calendar.getInstance()
calendar.add(Calendar.HOUR, hoursBefore * -1)

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows("PriceChangesFromPL")
def fields = [
        t1.key1().as("Material"),
        t1.key2().as("Pricelist"),
        t1.key3().as("ContractNumber"),
        t1.key4().as("ContractLine"),
        t1.key5().as("EffectiveDate"),
        t1.Processed
]
def filter = qapi.exprs().and(
        qapi.exprs().or(
                t1.Processed.equal(false),
                t1.Processed.isNull()
        ),
        t1.lastUpdateDate().greaterOrEqual(qapi.exprs().dateTime(calendar.getTime()))
)

def rows = qapi.source(t1, fields, filter).stream {
    it.collect { it }
}

api.local.processedRows = rows

def changesByContract = [:]
def changesByPricelist = [:]

def materials = []
def contractNumbers = []
def contractLines = []
def pricelists = []

def key
rows.each {
    if (it.Pricelist == "*") {
        key = it.Material + "|" + it.ContractNumber + "|" + it.ContractLine
        if (!changesByContract.containsKey(key)) changesByContract[key] = []
        changesByContract[key].add(it.EffectiveDate)
        materials.add(it.Material)
        contractNumbers.add(it.ContractNumber)
        contractLines.add(it.ContractLine)
    } else {
        key = it.Material + "|" + it.Pricelist
        if (!changesByPricelist.containsKey(key)) changesByPricelist[key] = []
        changesByPricelist[key].add(it.EffectiveDate)
        materials.add(it.Material)
        pricelists.add(it.Pricelist)
    }
}

api.local.changesByContract = changesByContract
api.local.changesByPricelist = changesByPricelist
api.local.materials = materials.toSet().toList()
api.local.contractNumbers = contractNumbers.toSet().toList()
api.local.contractLines = contractLines.toSet().toList()
api.local.pricelists = pricelists.toSet().toList()

return null