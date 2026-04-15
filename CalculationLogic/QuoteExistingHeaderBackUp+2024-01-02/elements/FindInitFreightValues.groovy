if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

if (!api.local.addedContracts) return

def freightFilters = []

def contracts = out.FindContractDSData
contracts?.each { contractNumber, lines ->
    lines?.each { line ->
        if (line?.PriceValidFrom) {
            freightFilters.add(Filter.and(
                    Filter.equal("SAPContractNumber", contractNumber),
                    Filter.equal("SAPLineID", line?.LineNumber),
                    Filter.equal("Material", line?.Material),
                    Filter.lessOrEqual("FreightValidFrom", line?.PriceValidFrom),
                    Filter.greaterOrEqual("FreightValidto", line?.PriceValidFrom)
            ))
        }
    }
}

return libs.QuoteLibrary.Query.findFreightValues(freightFilters)