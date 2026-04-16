//Generate custom events
if (api.local.newItemsA904 || api.local.newFreightItemsA904 || api.local.itemsToUpdateA904 || api.local.freightItemsToUpdateA904) {
    api.customEvent([
            Process : "CondRecordGenerated",
            TableNr : "A904"
    ], "ConditionRecord")
}

//Set status to ready
if (api.local.quoteIds) {
    libs.QuoteLibrary.Calculations.addOrUpdateQuotesForCRStatusToReady(api.local.quoteIds)
}