def exclusion = null

def exclusionSoldTo = api.local.exclusions
if (!exclusionSoldTo) return null

def shipToValue = api.local.itemData?.ShipTo
def ph1Value = api.local.itemData?.PH1
def materialValue = api.local.itemData?.Material

if (shipToValue) {
    def exclusionShipToSpecific = exclusionSoldTo[shipToValue]
    if (exclusionShipToSpecific) {
        exclusion = getExclusionStartingFromExclusionShipTo(exclusionShipToSpecific, ph1Value, materialValue)
    }
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