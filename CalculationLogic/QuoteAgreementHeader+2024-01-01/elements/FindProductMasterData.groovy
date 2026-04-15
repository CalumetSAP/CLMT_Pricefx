if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

def fields = ["sku", "label", "unitOfMeasure", "attribute6", "attribute14", "attribute15", "attribute16", "attribute17", "attribute18", "attribute19", "attribute20", "attribute21", "attribute24"]
def filter = Filter.in("sku", api.local.lineItemSkus)

def products = api.stream("P", null, fields, filter)?.withCloseable { it.collectEntries {
    [(it.sku): [
            Material            : it.sku,
            PH1Code             : it.attribute14 ?: null,
    ]]
}}

return products