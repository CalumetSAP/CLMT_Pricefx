if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

def sapContracts = []
def sapLines = []
def materials = []
def pvfByKey = [:]

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        if (line?.PriceValidFrom) {
            sapContracts << contractNumber
            sapLines     << line?.LineNumber
            materials    << line?.Material

            def key = "${contractNumber}|${line?.LineNumber}|${line?.Material}"
            pvfByKey.put(key.toString(), line?.PriceValidFrom)
        }
    }
}

if (!pvfByKey) return [:]

def allPvf = pvfByKey.values().flatten()
def minPvf = allPvf.min()
def maxPvf = allPvf.max()

Filter freightFilters = Filter.and(
        Filter.in("SAPContractNumber", sapContracts.unique().findAll{it!=null}),
        Filter.in("SAPLineID", sapLines.unique().findAll{it!=null}),
        Filter.in("Material", materials.unique().findAll{it!=null}),
        Filter.lessOrEqual("FreightValidFrom", maxPvf),
        Filter.greaterOrEqual("FreightValidto", minPvf)
)

return libs.QuoteLibrary.Query.findFreightValues(freightFilters, pvfByKey)