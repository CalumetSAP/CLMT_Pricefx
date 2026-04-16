if (api.isInputGenerationExecution() || api.isDebugMode()) return

final calculations = libs.PricelistLib.Calculations

List processingPLIds = calculations.getProcessingPriceListIdsForBasePricing()
if (!processingPLIds) return

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
                                                              "value"    : "BasePricePLOutboundDDL"]],
                                            "operator"    : "and",
                                            "_constructor": "AdvancedCriteria"],
                         "sortBy"        : ["-createDate"]]

List data = api.boundCall('local', 'admin.fetchjst?dataLocale=en', api.jsonEncode(dataPayload), false)?.responseBody?.response?.data

def jobId = data?.find()?.id
def completedPricelists, errorPricelists
if (!jobId) {
    errorPricelists = []
    completedPricelists = processingPLIds
} else {
    final Map dataPayload2 = ["endRow"        : 300,
                              "oldValues"     : null,
                              "operationType" : "fetch",
                              "startRow"      : 0,
                              "textMatchStyle": "exact",
                              "data"          : ["_constructor": "AdvancedCriteria",
                                                 "operator"    : "and", "criteria": [["criteria": [["fieldName": "jstId", "operator": "equals", "value": jobId.toString()]], "operator": "and", "_constructor": "AdvancedCriteria"]]]]

    List rows = api.boundCall('local', 'datamart.fetchcalcitems?dataLocale=en', api.jsonEncode(dataPayload2), false)?.responseBody?.response?.data

    errorPricelists = rows?.findAll { it.status == "FAILED" }?.key2
    completedPricelists = rows?.findAll { it.status == "COMPLETED" }?.key2
}

calculations.addOrUpdatePriceListForBasePricingStatusToReady(completedPricelists)
calculations.addOrUpdatePriceListForBasePricingStatusToError(errorPricelists)