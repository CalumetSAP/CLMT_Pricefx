if (api.isInputGenerationExecution()) return

def calcItem = dist?.calcItem
if (["OK", "SYNTAX_ERROR"].contains(calcItem?.Status)) return
def quoteId = calcItem?.Key2
def uniqueName = calcItem?.Value?.uniqueName
def quoteType = calcItem?.Value?.quoteType

def item = null
if (quoteType == "Existing") {
    def quoteIdNumber = uniqueName.replace(".Q", "")
    def lineItem = api.find("QLI", Filter.equal("clicId", quoteIdNumber)).find()
   item = api.getCalculableLineItem(quoteIdNumber + ".Q", lineItem.lineId)
}

def division = quoteType == "New"
        ? getNewDivision(quoteId, libs.QuoteConstantsLibrary.HeaderConfigurator.INPUTS_NAME, libs.QuoteConstantsLibrary.HeaderConfigurator.DIVISION_ID)
        : getExistingDivision(item, libs.QuoteConstantsLibrary.LineItem.DATA_SOURCE_VALUES_HIDDEN_ID, "Division")

addToCPT(uniqueName, division)

return

def addToCPT(uniqueName, division) {
    def cptId = api.findLookupTable("PendingDivisionQuotes")?.id
    if (cptId) {
        def pendingBody = [
                "data": [
                        "name"      : uniqueName,
                        "attribute1": division,
                        "attribute2": "PENDING"
                ],
                "operation": "add"
        ]
        api.boundCall("SystemUpdate", "/lookuptablemanager.add/${cptId.toString()}", api.jsonEncode(pendingBody).toString(), false)
    }
}

String getNewDivision(String quoteTypedId, String configuratorName, String inputName) {
    return api.getCalculableLineItemCollection(quoteTypedId)?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}

String getExistingDivision(item, String configuratorName, String inputName) {
    return item?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}