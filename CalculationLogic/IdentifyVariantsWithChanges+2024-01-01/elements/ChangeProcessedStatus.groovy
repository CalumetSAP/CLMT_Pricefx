if (api.isInputGenerationExecution()) return

def affectingChangeKeys = api.local.affectingChangeKeys as Set ?: []

def keyByContract, keyByPricelist
def rowsToUpdate = api.local.processedRows?.findAll { row ->
    keyByContract = row.Material + "|" + row.ContractNumber + "|" + row.ContractLine + "|" + row.EffectiveDate
    keyByPricelist = row.Material + "|" + row.Pricelist + "|" + row.EffectiveDate
    affectingChangeKeys.contains(keyByContract) || affectingChangeKeys.contains(keyByPricelist)
}

changeStatus(rowsToUpdate)

return null

def changeStatus(List processedRows) {
    def cptName = libs.QuoteConstantsLibrary.Tables.PRICE_CHANGES_FROM_PL
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")

    processedRows?.each {
        buildRowToAddOrUpdate(ppId, it)
    }
}

private def buildRowToAddOrUpdate(ppId, row) {
    def req = [data: [
            key1: row.Material,
            key2: row.Pricelist,
            key3: row.ContractNumber,
            key4: row.ContractLine,
            key5: row.EffectiveDate,
            attribute1: true
    ]]

    def body = api.jsonEncode(req)?.toString()

    def res = api.boundCall("SystemUpdate", "/lookuptablemanager.integrate/" + ppId, body, false)
}