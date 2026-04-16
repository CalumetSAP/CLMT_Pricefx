if (api.isInputGenerationExecution()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
api.global.selectedDivision = division

def lineItemSkus = []

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    lineItemSkus.add(lnProduct.sku)
}

api.local.lineItemSkus = lineItemSkus

return null