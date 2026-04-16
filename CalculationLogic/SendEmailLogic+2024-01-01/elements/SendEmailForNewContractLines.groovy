def newContractsLines = input["newContractsLines"] as List
def sapContractsMap = input["sapContractsData"] as Map

if (!newContractsLines || !sapContractsMap) return

def emails = getEmails()
def lines = getLines(newContractsLines)
def businessRules = getBusinessRules()

def isEnabled, email, subject, text, sapContractData
lines.each {
    isEnabled = it.Division == "20" ? businessRules?.get("EnableNewContractEmailForDivision20") : businessRules?.get("EnableNewContractEmailForDivision30")
    if (!isEnabled) return

    sapContractData = sapContractsMap?.get(it.LineID)
    email = emails?.get(it.Division)
    subject = it.QuoteID + " and " + it.LineID + " contract has been created or updated"
    text = it.QuoteID + " and " + it.LineID + " contract has been created or updated. SAP Contract is " + sapContractData?.SAPContractNumber + " and " + sapContractData?.SAPLineID + "."
    api.sendEmail(email as String, subject, text)
}

def getBusinessRules() {
    def filters = [
            Filter.in("name", "EnableNewContractEmailForDivision20", "EnableNewContractEmailForDivision30")
    ]
    return api.findLookupTableValues("BusinessRules", ["name", "attribute3"], null, *filters)?.collectEntries {
        [(it.name): it.attribute3]
    }
}

def getEmails() {
    def filters = [
            Filter.equal("key1", "Pricing")
    ]
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.WORKFLOW_EMAILS, ["key2", "attribute1"], null, *filters)?.collectEntries {
        [(it.key2): it.attribute1]
    }
}

def getLines(List newContractsLines) {
    final tablesConstants = libs.QuoteConstantsLibrary.Tables

    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_QUOTES)

    def customFilter = Filter.and(
            Filter.in("LineID", newContractsLines)
    )

    def query = ctx.newQuery(dm, false)
            .select("QuoteID", "QuoteID")
            .select("LineID", "LineID")
            .select("Division", "Division")
            .where(customFilter)

    def result = ctx.executeQuery(query)
    return data = result?.getData()?.findAll()
}