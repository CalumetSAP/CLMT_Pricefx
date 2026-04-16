if (!quoteProcessor.isPostPhase()) return

String commandExecution = api.currentContext().commandName
if (commandExecution != "submit") return

final dateUtils = libs.QuoteLibrary.DateUtils

// Update Dates
def submissionDate = dateUtils.getToday()
quoteProcessor.updateField("targetDate", submissionDate)

def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["ExpiryDate"]?.values()?.find() as Integer : null
def expiryDate = dateUtils.sumDays(submissionDate, days)
quoteProcessor.updateField("expiryDate", expiryDate)

// Run CF to convert to deal (only if the post approval will not run because there are not approvers)
if (!hasApprovers(quoteProcessor)) {
    def cf = api.find("CF", 0, api.getMaxFindResultsLimit(), null,
            Filter.and(
                    Filter.equal("draft","false"),
                    Filter.equal("uniqueName","UpdateQuotesToDeal")))
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

Boolean hasApprovers (quoteProcessor) {
    final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
    def outputs
    for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
        outputs = lnProduct.outputs
        if (outputs.find { it.resultName == lineItemOutputsConstants.APPROVER_ID }?.result
                || outputs.find { it.resultName == lineItemOutputsConstants.FORMULA_APPROVER_ID }?.result) {
            return true
        }
    }
    return false
}