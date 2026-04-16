if (api.isInputGenerationExecution()) return

def newQuotes = out.FindNewQuotes ?: []
def existingQuotes = out.FindExistingQuotes ?: []

def lines = newQuotes + existingQuotes

addLinesToCPT(lines)

return

def addLinesToCPT(List lines) {
    def cptId = api.findLookupTable("PendingDivisionQuotes")?.id
    if (cptId) {
        def pendingBody = [
                "data": [
                        "attribute2": "PENDING"
                ],
                "operation": "add"
        ]
        for (line in lines) {
            pendingBody.data.name = line.uniqueName
            pendingBody.data.attribute1 = line.division
            api.boundCall("SystemUpdate", "/lookuptablemanager.add/${cptId.toString()}", api.jsonEncode(pendingBody).toString(), false)
        }
    }
}