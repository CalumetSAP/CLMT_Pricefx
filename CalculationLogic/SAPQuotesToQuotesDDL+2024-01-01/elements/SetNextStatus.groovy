if (api.isInputGenerationExecution() || api.isDebugMode()) return

final quoteCalculations = libs.QuoteLibrary.Calculations

def processingSAPUpdateRows = quoteCalculations.getProcessingSAPUpdateRows()
if (processingSAPUpdateRows) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("SAPQuotes")
    def lineIds, query
    processingSAPUpdateRows.quoteId.each { quoteId ->
        lineIds = api.getCalculableLineItemCollection(quoteId)?.lineItems?.lineId ?: []
        query = ctx.newQuery(dm, false)
                .select("LineID", "LineID")
                .where(
                        Filter.in("LineID", lineIds),
                        Filter.isNull("SAPContractNumber")
                )
        if (ctx.executeQuery(query)?.getData()?.collect()) {
            quoteCalculations.addOrUpdateStatusToReWaitingSAPUpdateQuotes(quoteId)
        } else {
            //Once all the rows have the SAPContractNumber, go to the next status and build condition records
            quoteCalculations.addOrUpdateStatusToPendingConditionRecord(quoteId)
            runCalculationFlow("AddNewQuotesConditionRecords")
        }
    }
}

return null

def runCalculationFlow (cfName) {
    def cf = api.find("CF", 0, api.getMaxFindResultsLimit(), null,
            Filter.and(
                    Filter.equal("draft", "false"),
                    Filter.equal("uniqueName", cfName)))
            .collect{
                ["flowId": it.flowId, "flowItemId": api.jsonDecode(it.configuration)?.entries.find()?.id]
            }?.find()
    if (cf) {
        Map requestBody = [
                "data": [
                        "configuration": "{}",
                        "flowId": cf.flowId,
                        "flowItemId": cf.flowItemId,
                        "traitType": "START_IMMEDIATELY"
                ],
                "oldValues": null,
                "operationType": "add",
                "textMatchStyle": "exact"
        ]

        try {
            def response = api.boundCall("local", "/add/CFT", api.jsonEncode(requestBody), true)
            api.logInfo("Response", response)
        } catch (e) {
            api.logInfo("Error", e)
        }
    }
}