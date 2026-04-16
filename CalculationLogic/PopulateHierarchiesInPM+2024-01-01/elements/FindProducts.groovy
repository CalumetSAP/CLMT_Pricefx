if (api.isInputGenerationExecution()) return

final calculations = libs.QuoteLibrary.Calculations

def fields = ["sku", "attribute1"]

def result = api.stream("P", null, fields, null)?.withCloseable { it.collectEntries {
    [(it.sku): [
            PH1: calculations.getPH1(it.attribute1),
            PH2: calculations.getPH2(it.attribute1),
            PH3: calculations.getPH3(it.attribute1),
            PH4: calculations.getPH4(it.attribute1),
    ]]
}}

api.global.products = result

return null