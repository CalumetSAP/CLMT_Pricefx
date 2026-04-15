if (api.isInputGenerationExecution()) return

Boolean isExistingQuote = quote?.quoteType == "ExistingContractUpdate"

if (!isExistingQuote) return

def cf = api.find("CF", 0, api.getMaxFindResultsLimit(), null,
        Filter.and(
                Filter.equal("draft","false"),
                Filter.equal("uniqueName","AddExistingQuotesConditionRecords")))
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

return null