api.local.lineIds = libs.QuoteLibrary.Calculations.getPendingLineIDsForCRIDs() ?: []

if (!api.local.lineIds) {
    api.abortCalculation()
}