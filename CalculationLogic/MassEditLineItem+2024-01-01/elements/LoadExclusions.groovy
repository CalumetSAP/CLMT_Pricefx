String soldToValue = out.LoadQuotes?.SoldTo
String shipToValue = out.LoadQuotes?.ShipTo
String materialValue = api.local.material
String ph1Value = out.LoadProducts?.PH1

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    List<Object> quotes = api.global.quotes?.collect()
    HashSet<String> soldTos = new HashSet<>()
    soldTos.add("*")
    soldTos.addAll(quotes?.SoldTo ?: [])
    soldTos.remove(null)

    api.global.exclusions = libs.PricelistLib.Common.getExclusions(soldTos)
}

def exclusion = null

def exclusionSoldTo = api.global.exclusions[soldToValue] ?: api.global.exclusions["*"]
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

return exclusion?.find()

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