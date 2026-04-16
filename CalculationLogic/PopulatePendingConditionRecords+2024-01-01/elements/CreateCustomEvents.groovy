if (api.isInputGenerationExecution()) return

def conditionRecordSetMap = out.LoadConditionRecordSetMap

def tablesUpdated = new HashSet()
List readyKeys = []
def itemToAdd, table
api.local.pendingConditionRecords.each { key, value ->
    itemToAdd = api.jsonDecode(value.Data)
    table = value.Table ?: "A904"
    itemToAdd.conditionRecordSetId = conditionRecordSetMap[table]
    api.trace("itemToAdd", itemToAdd)
    if (table == "A901") {
        api.addOrUpdate("CRCI3", itemToAdd)
    } else {
        api.addOrUpdate("CRCI5", itemToAdd)
    }
    readyKeys.add(key)
    tablesUpdated.add(table)
}

if (readyKeys) {
    for (tableNr in tablesUpdated) {
        api.customEvent([
                Process : "CondRecordGenerated",
                TableNr : tableNr
        ], "ConditionRecord")
    }

    updateStatusToReady(readyKeys)
}

return

def updateStatusToReady(List keys) {
    def cptName = libs.QuoteConstantsLibrary.Tables.PENDING_CONDITION_RECORDS
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")
    keys.each { key ->
        buildRowToAddOrUpdate(ppId, key)
    }
}

private def buildRowToAddOrUpdate(ppId, key) {
    def attributeExtension = [
            "Status": libs.QuoteLibrary.Calculations.READY_STATUS,
    ]

    def req = [data: [
            header: ['lookupTable', 'name', 'attributeExtension'],
            data : [[ppId, key, api.jsonEncode(attributeExtension)]]
    ]]

    def body = api.jsonEncode(req)?.toString()

    api.boundCall("SystemUpdate", "/loaddata/JLTV", body, false)
}