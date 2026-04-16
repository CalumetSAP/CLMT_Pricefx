def addNewQuoteCustomEvent (uuid) {
    api.customEvent(buildSAPContractCustomEventBody(uuid, "New", "New quote"), "SAPContract")
}

def addExistingQuoteCustomEvent (uuid) {
    api.customEvent(buildSAPContractCustomEventBody(uuid, "Change", "Existing quote"), "SAPContract")
}

def addPriceMaintenanceCustomEvent (uuid) {
    api.customEvent(buildSAPContractCustomEventBody(uuid, "Change", "Price maintenance"), "SAPContract")
}

private def buildSAPContractCustomEventBody (uuid, process, type) {
    return [
            UUID    : uuid,
            Process : process,
            Type    : type,
    ]
}