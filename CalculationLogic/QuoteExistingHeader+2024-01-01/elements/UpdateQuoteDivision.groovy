if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def selectedContracts = api.local.addedContracts
def removedContracts = api.local.removedContracts?.collect { it.split("\\|").getAt(0).trim() } as List

def division
if (selectedContracts) {
    division = api.local.division
} else if (!removedContracts) {
    for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
        if (lnProduct.folder || api.local.removedLineIds?.contains(lnProduct.lineId) || division) continue

        def dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

        division = dsData.Division
    }
}

def divisionMap = out.FindDivision

def completeDivision = division ? divisionMap?.get(division) : null
quoteProcessor.updateField("attributeExtension___Division", completeDivision)