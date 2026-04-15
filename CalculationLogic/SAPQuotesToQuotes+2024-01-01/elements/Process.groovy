if(api.isInputGenerationExecution()) return

def source = api. getDatamartRowSet("source")
def target = api. getDatamartRowSet("target")

def acum = 0
List pendingLineIds = []
List updatedLineIDs = []
Map sapQuotesGrouped = new HashMap()
def quotes, quoteRowToUpdate, sapQuoteRow
while (source?.next()) {
    def row = source?.getCurrentRow()
    if (row.SAPContractNumber) {
        pendingLineIds.add(row.LineID)
        sapQuotesGrouped.put(row.LineID, [
                SAPContractNumber   : row.SAPContractNumber,
                SAPLineID           : row.SAPLineID
        ])
        acum++

        //This is to process in batches
        if (acum == 2000) {
            quotes = getQuotesKeys(pendingLineIds)
            quotes.each { key, value ->
                sapQuoteRow = sapQuotesGrouped[key]
                quoteRowToUpdate = value
                quoteRowToUpdate.SAPContractNumber = sapQuoteRow.SAPContractNumber
                quoteRowToUpdate.SAPLineID = sapQuoteRow.SAPLineID
                target.addRow(quoteRowToUpdate)
            }
            updatedLineIDs.addAll(quotes.keySet())
            acum = 0
            pendingLineIds = []
        }
    }
}
//Process remaining items
if (acum != 0) {
    quotes = getQuotesKeys(pendingLineIds)
    quotes.each { key, value ->
        sapQuoteRow = sapQuotesGrouped[key]
        quoteRowToUpdate = value
        quoteRowToUpdate.SAPContractNumber = sapQuoteRow.SAPContractNumber
        quoteRowToUpdate.SAPLineID = sapQuoteRow.SAPLineID
        target.addRow(quoteRowToUpdate)
    }
    updatedLineIDs.addAll(quotes.keySet())
}
api.logInfo("ALAN - updatedLineIDs", updatedLineIDs)
if (updatedLineIDs) {
    updateFlagsToY(updatedLineIDs)
    libs.QuoteLibrary.Calculations.addLineIDsForCRStatusToPendingUsingBoundCall(updatedLineIDs)
    runCalculationFlow("AddNewQuotesConditionRecords")
    sendEmail(updatedLineIDs, sapQuotesGrouped)
}

Map getQuotesKeys (List lineIds) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("Quotes")

    def query = ctx.newQuery(dm, false)
            .selectAll(true)
            .where(Filter.in("LineID", lineIds))

    return ctx.executeQuery(query)?.getData()?.collectEntries {
        [(it.LineID): it]
    } ?: [:]
}

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

def updateFlagsToY(updatedLineIDs) {
    def sapQuotesDSTypedId = api.find("DMDS", 0, 1, null, Filter.equal("uniqueName", "SAPQuotes"))?.find()?.typedId
    def body = [
            "data": [
                    "filterCriteria": [
                            "_constructor": "AdvancedCriteria",
                            "operator": "and",
                            "criteria": [
                                    [
                                            "fieldName": "LineID",
                                            "operator": "inSet",
                                            "value": updatedLineIDs
                                    ]
                            ]
                    ],
                    "massEditRecords": [
                            [
                                    "fieldName": "UpdateFlag",
                                    "massEditOperator": "=",
                                    "fieldValue": "Y",
                                    "precision": null
                            ]
                    ]
            ]
    ]

    api.boundCall("local", "/datamart.massedit/${sapQuotesDSTypedId}", api.jsonEncode(body).toString(), true)
}

def sendEmail(updatedLineIDs, sapQuotesGrouped) {
    def body = [
            "data": [
                    "newContractsLines": updatedLineIDs,
                    "sapContractsData" : sapQuotesGrouped,
            ]
    ]
    def response = api.boundCall("SystemUpdate", "/formulamanager.executeformula/SendEmailLogic", api.jsonEncode(body).toString(), false)
}