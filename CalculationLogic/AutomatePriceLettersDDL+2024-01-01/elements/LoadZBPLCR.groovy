if (api.isInputGenerationExecution()) return

def salesOrgs = api.global.salesOrgList
def pricelists = api.global.pricelistList
def materials = api.global.materialList

def qapi = api.queryApi()
def t1 = qapi.tables().conditionRecords("A932")
def fields = [
        t1.key2(),
        t1.key3(),
        t1.key4(),
        t1.key5(),
        t1.validFrom(),
        t1.validTo(),
        t1.unitOfMeasure(),
        t1.priceUnit(),
        t1.conditionValue(),
        t1.currency(),
        t1.Scales.as("attribute2"),
        t1.ScaleUOM.as("attribute3"),
        t1."Integration Flag".as("attribute4"),
        t1."Superseded Flag".as("attribute5"),
        t1.lastUpdateDate()
]
def filters = []
filters.add(t1.key1().equal("ZBPL"))
if (salesOrgs) filters.add(t1.key2().in(salesOrgs as List))
if (pricelists) filters.add(t1.key4().in(pricelists as List))
if (materials) filters.add(t1.key5().in(materials as List))

def customFilters = qapi.exprs().and(*filters)

api.global.zbplCR = qapi.source(t1, fields, customFilters).stream {
    it.collect { it }.groupBy { [it.key2, it.key4, it.key5] }
}

return null