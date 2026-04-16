String soldToValue = out.LoadQuotes.SoldTo
String shipToValue = out.LoadQuotes.ShipTo
String materialValue = api.local.sku
String ph1Value = out.LoadProduct.PH1

if (api.global.isFirstRow || api.global.isFirstIterationFirstRow) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("Exclusions")
    def fields = [
            t1.key1(),
            t1.key2(),
            t1.key3(),
            t1.key4(),
            t1.NoOfDays.as("attribute1"),
            t1.Protection.as("attribute2"),
            t1."Movement Timing".as("attribute3"),
            t1."Movement Start Month".as("attribute4"),
            t1."Movement Day".as("attribute5"),
            t1.Comment.as("attribute6"),
    ]

    def rows = qapi.source(t1, fields).stream { it.collect {it } }

    api.global.exclusions = rows?.groupBy { it.key2 }?.collectEntries { soldTo, values1 ->
        [soldTo, values1?.groupBy { it.key3 }?.collectEntries { shipTo, values2 ->
            [shipTo, values2?.groupBy { it.key1 }?.collectEntries { ph1, values3 ->
                [ph1, values3?.groupBy { it.key4 }?.collectEntries { material, values4 ->
                    [material, values4]
                }]
            }]
        }]
    }
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

def getExclusionsData() {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("Exclusions")
    def fields = [
            t1.key1(),
            t1.key2(),
            t1.key3(),
            t1.key4(),
            t1.NoOfDays.as("attribute1"),
            t1.Protection.as("attribute2"),
            t1."Movement Timing".as("attribute3"),
            t1."Movement Start Month".as("attribute4"),
            t1."Movement Day".as("attribute5"),
            t1.Comment.as("attribute6"),
    ]

    def rows = qapi.source(t1, fields).stream { it.collect {it } }

    return rows?.groupBy { it.key2 }?.collectEntries { soldTo, values1 ->
        [soldTo, values1?.groupBy { it.key3 }?.collectEntries { shipTo, values2 ->
            [shipTo, values2?.groupBy { it.key1 }?.collectEntries { ph1, values3 ->
                [ph1, values3?.groupBy { it.key4 }?.collectEntries { material, values4 ->
                    [material, values4]
                }]
            }]
        }]
    }
}