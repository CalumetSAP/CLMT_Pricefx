import net.pricefx.common.api.InputType

if (!quoteProcessor.isPrePhase() || api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def removedContracts = api.local.removedContracts?.collect { it.split("\\|").getAt(0).trim() } as List

if (!removedContracts && !api.local.newFilterChanged && !api.local.addedContracts) return

Set previousLineItems = quoteProcessor.getHelper().getRoot().getInputByName("PreviousLineItemHiddenInput")?.value ?: [] as Set
Set currentLineItems = api.local.contractLineSet ?: [] as Set

def linesToRemove = previousLineItems - currentLineItems
api.local.linesToAdd = currentLineItems - previousLineItems

if (linesToRemove) {
    def dsData, key
    def removedLineIds = []
    quoteProcessor.getQuoteView().lineItems.findAll {
        dsData = calculations.getInputValue(it, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
        key = dsData?.get("SAPContractNumber") + "|" + dsData?.get("SAPLineId")
        linesToRemove.contains(key)
    }.each {
        removedLineIds.add(it.lineId)
        quoteProcessor.deleteItem(it.lineId)
    }

    api.local.removedLineIds = removedLineIds
}

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : "PreviousLineItemHiddenInput",
        "value": currentLineItems,
        "type" : InputType.HIDDEN,
])

return null