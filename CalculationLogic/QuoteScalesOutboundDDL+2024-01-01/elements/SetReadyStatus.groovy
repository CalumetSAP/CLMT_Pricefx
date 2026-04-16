if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.QuoteLibrary.Calculations

List<Object> processingQuoteIds = calculations.getQuoteIdsForScalesByStatus(calculations.PROCESSING_STATUS)
if (!processingQuoteIds) return

final Map dataPayload = ["endRow"        : 1,
                         "oldValues"     : null,
                         "operationType" : "fetch",
                         "startRow"      : 0,
                         "textMatchStyle": "exact",
                         "data"          : ["criteria"    : [["fieldName": "trackerType",
                                                              "operator" : "equals",
                                                              "value"    : "PADATALOAD"],
                                                             ["fieldName": "status",
                                                              "operator" : "equals",
                                                              "value"    : "PROCESSING"],
                                                             ["fieldName": "jobName",
                                                              "operator" : "iContains",
                                                              "value"    : "QuoteScalesOutboundDDL"]],
                                            "operator"    : "and",
                                            "_constructor": "AdvancedCriteria"],
                         "sortBy"        : ["-createDate"]]

List data = api.boundCall('local', 'admin.fetchjst?dataLocale=en', api.jsonEncode(dataPayload), false)?.responseBody?.response?.data

def jobId = data?.find()?.id
def completedQuotes, errorQuotes
if (!jobId) {
    errorQuotes = []
    completedQuotes = processingQuoteIds
} else {
    final Map dataPayload2 = ["endRow"        : 300,
                              "oldValues"     : null,
                              "operationType" : "fetch",
                              "startRow"      : 0,
                              "textMatchStyle": "exact",
                              "data"          : ["_constructor": "AdvancedCriteria",
                                                 "operator"    : "and", "criteria": [["criteria": [["fieldName": "jstId", "operator": "equals", "value": jobId.toString()]], "operator": "and", "_constructor": "AdvancedCriteria"]]]]

    List rows = api.boundCall('local', 'datamart.fetchcalcitems?dataLocale=en', api.jsonEncode(dataPayload2), false)?.responseBody?.response?.data

    errorQuotes = rows?.findAll { it.status == "FAILED" }?.key2
    completedQuotes = rows?.findAll { it.status == "COMPLETED" }?.key2
}


calculations.addOrUpdateQuoteForScalesStatusToReady(completedQuotes)
calculations.addOrUpdateQuoteForScalesStatusToError(errorQuotes)