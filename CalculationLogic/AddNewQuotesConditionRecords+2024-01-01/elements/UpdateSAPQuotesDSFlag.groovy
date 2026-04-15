//if (api.local.lineIds) {
//    def sapQuotesDSTypedId = api.find("DMDS", 0, 1, null, Filter.equal("uniqueName", "SAPQuotes"))?.find()?.typedId
//    def body = [
//            "data": [
//                    "filterCriteria": [
//                            "_constructor": "AdvancedCriteria",
//                            "operator": "and",
//                            "criteria": [
//                                    [
//                                            "fieldName": "LineID",
//                                            "operator": "inSet",
//                                            "value": api.local.lineIds
//                                    ]
//                            ]
//                    ],
//                    "massEditRecords": [
//                            [
//                                    "fieldName": "UpdateFlag",
//                                    "massEditOperator": "=",
//                                    "fieldValue": "Y",
//                                    "precision": null
//                            ]
//                    ]
//            ]
//    ]
//
//    api.boundCall("local", "/datamart.massedit/${sapQuotesDSTypedId}", api.jsonEncode(body).toString(), true)
//}