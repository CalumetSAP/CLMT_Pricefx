if (api.isInputGenerationExecution()) return

def quotes = out.GetQuotes
quotes.each {
    api.boundCall("SystemUpdate", "/quotemanager.convert/${it}", "", true)
}

// Mark as "Finalized" in the "Finalize Quote" tab
// 1. Get the "Finalize Quote" custom form data
def finalizeQuoteInput
def finalizeQuoteCFOs = api.find("CFO",0,api.getMaxFindResultsLimit(), "-lastUpdateDate", Filter.in("parentTypedId", quotes))
        ?.findAll {
            finalizeQuoteInput = it.inputs?.find { it.name == "FinalizeQuoteInput" }
            // FinalizeQuoteInput exists and value is false
            return finalizeQuoteInput && !finalizeQuoteInput.value
        }
        ?.collect {
            it.inputs?.find { it.name == "FinalizeQuoteInput" }?.value = true
            return [data: it]
        }

// 2. Update custom form "Finalize quote" checkbox to true
finalizeQuoteCFOs.each {
    def response = api.boundCall("local", "/customform.update/${it.data.typedId}", api.jsonEncode(it), false)
    api.trace("response", response)
}

// 3. Recalculate custom form to update inputs to set readOnly to true
Map requestBody
finalizeQuoteCFOs.each {
    requestBody = [
            data: [
                    inputs: it.data.inputs
            ]
    ]
    def response = api.boundCall("local", "/customform.recalculate/${it.data.typedId}", api.jsonEncode(requestBody), false)
    api.trace("response", response)
}