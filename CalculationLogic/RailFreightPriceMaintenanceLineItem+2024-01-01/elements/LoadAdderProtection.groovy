if (api.global.isFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("FreightFirmAdderProtection")

    def rows = qapi.source(t1, [t1.key1(), t1.key2(), t1.key3(), t1.key4()]).stream {it.collect {it } } ?: []

    def adderProtectionMap = [:]

    def soldToMap, shipToMap, ph1Map
    rows.each {
        soldToMap = adderProtectionMap[it.key2] ?: (adderProtectionMap[it.key2] = [:])
        shipToMap = soldToMap[it.key3] ?: (soldToMap[it.key3] = [:])
        ph1Map    = shipToMap[it.key1] ?: (shipToMap[it.key1] = [:])

        ph1Map[it.key4] = "Y"
    }

    api.global.adderProtection = adderProtectionMap
}

def soldTo = out.LoadQuotes.SoldTo
def shipTo = out.LoadQuotes.ShipTo
def ph1 = out.LoadProduct.PH1
def material = api.local.sku

return findAdderProtection(api.global.adderProtection as Map, soldTo, shipTo, ph1, material) ?: "N"

def findAdderProtection(Map adderProtectionMap, soldTo, shipTo, ph1, material) {
    def adderProtection = null

    if (soldTo) {
        def adderSoldToSpecific = adderProtectionMap[soldTo] as Map
        if (adderSoldToSpecific) {
            adderProtection = getAdderStartingFromAdderSoldTo(adderSoldToSpecific, shipTo, ph1, material)
        }
    }

    if (!adderProtection) {
        def adderSoldToNotSpecific = adderProtectionMap["*"] as Map
        if (adderSoldToNotSpecific) {
            adderProtection = getAdderStartingFromAdderSoldTo(adderSoldToNotSpecific, shipTo, ph1, material)
        }
    }

    return adderProtection
}

def getAdderStartingFromAdderSoldTo(Map soldToMap, shipTo, ph1, material) {
    def adderProtection = null

    if (shipTo) {
        def adderShipToSpecific = soldToMap[shipTo] as Map
        if (adderShipToSpecific) {
            adderProtection = getAdderStartingFromAdderShipTo(adderShipToSpecific, ph1, material)
        }
    }

    if (!adderProtection) {
        def adderShipToNotSpecific = soldToMap["*"] as Map
        if (adderShipToNotSpecific) {
            adderProtection = getAdderStartingFromAdderShipTo(adderShipToNotSpecific, ph1, material)
        }
    }

    return adderProtection
}

def getAdderStartingFromAdderShipTo(Map shipToMap, String ph1, String material) {
    def adderProtection = null
    def adderPH1Specific = shipToMap[ph1]
    if (adderPH1Specific) {
        adderProtection = adderPH1Specific[material] ?: adderPH1Specific["*"]
    }
    if (!adderProtection) {
        def adderPH1NotSpecific = shipToMap["*"]
        if (adderPH1NotSpecific) {
            adderProtection = adderPH1NotSpecific[material] ?: adderPH1NotSpecific["*"]
        }
    }

    return adderProtection
}