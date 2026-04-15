def secKey = []

String material = api.local.sku
def quotes = api.global.groupedQuotes[material]
String ph1Value = out.LoadProducts?.ProductHierarchy?.take(2)

quotes?.each { quote ->
    if (!anyExclusion(api.global.exclusionsWithXOnNoOfDays, quote.SoldTo, quote.ShipTo, ph1Value, material)) {
        secKey << (quote.SoldTo?:"")+"-"+(quote.ShipTo?:"")+"-"+(quote.SAPContractNumber?:"")+"-"+(quote.SAPLineID?:"")
    }
}

return secKey?.unique()

def anyExclusion (exclusions, soldToValue, shipToValue, ph1Value, materialValue) {
    def exclusion = null
    def exclusionSoldTo = exclusions[soldToValue] ?: exclusions["*"]
    if (!exclusionSoldTo) return null
    def exclusionShipToSpecific = exclusionSoldTo[shipToValue]
    if (exclusionShipToSpecific) {
        exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToSpecific, ph1Value, materialValue)
    }
    if (!exclusion) {
        def exclusionShipToNotSpecific = exclusionSoldTo["*"]
        if (exclusionShipToNotSpecific) {
            exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToNotSpecific, ph1Value, materialValue)
        }
    }

    return exclusion?.any()
}

def getExclusionStartingFromExclusionShipTo (exclusionShipTo, String ph1Value, String materialValue) {
    def exclusion = null
    def exclusionPH1Specific = exclusionShipTo[ph1Value]
    if (exclusionPH1Specific) {
        exclusion = exclusionPH1Specific[materialValue] ?: exclusionPH1Specific["*"]
    }
    if (!exclusion) {
        def exclusionPH1NotSpecific = exclusionShipTo["*"]
        if (exclusionPH1NotSpecific) {
            exclusion = exclusionPH1NotSpecific[materialValue] ?: exclusionPH1NotSpecific["*"]
        }
    }

    return exclusion
}