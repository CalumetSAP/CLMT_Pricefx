//Generate custom events
if (api.local.newItemsA904) {
    api.customEvent([
            Process : "CondRecordGenerated",
            TableNr : "A904"
    ], "ConditionRecord")
}

//Set status to ready
if (api.local.lineIds) {
    libs.QuoteLibrary.Calculations.addOrUpdateLineIDsForCRStatusToReady(api.local.lineIds)
}