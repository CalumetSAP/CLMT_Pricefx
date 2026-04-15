if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

def materials = []
def pricelists = []

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        def pricelist = line?.PriceListPLT
        if (pricelist && line?.Material) {
            materials.add(line?.Material)
            pricelists.add(pricelist)
        }
    }
}

def qapi = api.queryApi()
def t1 = qapi.tables().conditionRecords("A932")

if (!pricelists || !materials) return [:]

def basePricingCRFilters = qapi.exprs().and(
        t1.key4().in(pricelists?.unique()?.findAll()),
        t1.key5().in(materials?.unique()?.findAll()),
)

if (!basePricingCRFilters) return [:]

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
        t1.Scales,
        t1.ScaleUOM,
        t1."Integration Flag",
        t1."Superseded Flag",
        t1.lastUpdateDate()
]
def filter = qapi.exprs().and(
        t1.key1().equal("ZBPL"),
        basePricingCRFilters

)

def data = qapi.source(t1, fields, filter)
        .sortBy { cols -> [qapi.orders().descNullsLast(cols.lastUpdateDate)] }
        .stream {
            it.collect {
                return [
                        key2          : it.key2,
                        key3          : it.key3,
                        key4          : it.key4,
                        key5          : it.key5,
                        validFrom     : it.validFrom,
                        validTo       : it.validTo,
                        unitOfMeasure : it.unitOfMeasure,
                        priceUnit     : it.priceUnit,
                        conditionValue: it.conditionValue,
                        currency      : it.currency,
                        attribute2    : it.Scales,
                        attribute3    : it.ScaleUOM,
                        attribute4    : it."Integration Flag",
                        attribute5    : it."Superseded Flag",
                        lastUpdateDate: it.lastUpdateDate,
                ]
            }
        }

def zbplCondRecMap = [:]
def key
data?.each { row ->
    key = row.key2 + "|" + row.key4 + "|" + row.key5
    if (!zbplCondRecMap.containsKey(key)) zbplCondRecMap[key] = []
    zbplCondRecMap[key].add(row)
}

return zbplCondRecMap