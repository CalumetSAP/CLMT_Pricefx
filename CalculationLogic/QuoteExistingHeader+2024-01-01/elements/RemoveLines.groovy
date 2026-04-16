if (!quoteProcessor.isPrePhase() || api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def removedContracts = api.local.removedContracts?.collect { it.split("\\|").getAt(0).trim() } as List

if (!removedContracts) return

def removedLineIds = []
quoteProcessor.getQuoteView().lineItems.findAll {
    removedContracts.contains(calculations.getInputValue(it, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)?.get("SAPContractNumber"))
}.each {
    removedLineIds.add(it.lineId)
    quoteProcessor.deleteItem(it.lineId)
}

api.local.removedLineIds = removedLineIds

return null