if(api.isInputGenerationExecution()) return

def target = api.getDatamartRowSet("target")

def pendingPriceListIds = libs.PricelistLib.Calculations.getPendingPriceListIdsForScales()
final query = libs.QuoteLibrary.Query

if (!pendingPriceListIds) return

def plTypes = findPricelistTypes(pendingPriceListIds)

def lineIds, plItems, scaleUOM, priceUOM, quoteDSKeys, plItemAux
for (plId in pendingPriceListIds) {
    lineIds = new HashSet<>()
    plItems = [:]
    if (plTypes[plId] != libs.PricelistLib.Constants.FREIGHT_MAINTENANCE_PL_TYPE && plTypes[plId] != libs.PricelistLib.Constants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE) {
        libs.PricelistLib.Common.getAllPLItems(plId)?.findAll {
            (it["Scale Qty 1"] != null && it["Price 1"] != null) || (it["Scale Qty 2"] != null && it["Price 2"] != null) || (it["Scale Qty 3"] != null && it["Price 3"] != null) || (it["Scale Qty 4"] != null && it["Price 4"] != null) || (it["Scale Qty 5"] != null && it["Price 5"] != null)
        }?.each {plItem ->
            scaleUOM = plItem["Scale UOM"]
            priceUOM = plItem["Pricing UOM"]
            quoteDSKeys = getQuoteIdAndLineId(plItem["Quote DS Keys"])

            lineIds.add(quoteDSKeys.LineId)
            for (scale in getScales(plItem)) {
                plItems[[quoteDSKeys.QuoteId, quoteDSKeys.LineId, scale.scaleQty?.toString()]] = [
                        QuoteID : quoteDSKeys.QuoteId,
                        LineID  : quoteDSKeys.LineId,
                        ScaleID : scale.scaleQty?.toString(),
                        ScaleQty: scale.scaleQty,
                        ScaleUOM: scaleUOM,
                        Price   : scale.price,
                        PriceUOM: priceUOM
                ]
            }
        }

        lineIds.collate(2000).each { linesIds ->
            for (quoteScaleRow in query.getQuoteScalesRows(linesIds)) {
                plItemAux = plItems.remove([quoteScaleRow.QuoteID, quoteScaleRow.LineID, quoteScaleRow.ScaleID])
                //Update existing Quote Scales item
                if (plItemAux) {
                    def result = target.addRow(plItemAux)
                } else { //Remove existing Quote Scales item
                    quoteScaleRow.isDeleted = true
                    target.addRow(quoteScaleRow)
                }
            }
        }

        //Add new Quote Scales items
        plItems.each {
            target.addRow(it.value)
        }
    } else {
        def filteredItems = libs.PricelistLib.Common.getAllPLItems(plId)?.findAll {
            (it["Scale Qty 1"] != null && it["Price 1"] != null) || (it["Scale Qty 2"] != null && it["Price 2"] != null) || (it["Scale Qty 3"] != null && it["Price 3"] != null) || (it["Scale Qty 4"] != null && it["Price 4"] != null) || (it["Scale Qty 5"] != null && it["Price 5"] != null)
        }
        def quoteDSData = getQuoteIdAndLineIdFromQuotesDS(filteredItems)

        filteredItems?.each {plItem ->
            scaleUOM = plItem["Scale UOM"]
            priceUOM = plItem["Product UOM"]
            quoteDSKeys = quoteDSData[plItem["Contract #"] + "|" + plItem["Contract Line"]]

            lineIds.add(quoteDSKeys.LineId)
            for (scale in getScales(plItem)) {
                plItems[[quoteDSKeys?.QuoteId, quoteDSKeys?.LineId, scale.scaleQty?.toString()]] = [
                        QuoteID : quoteDSKeys?.QuoteId,
                        LineID  : quoteDSKeys?.LineId,
                        ScaleID : scale.scaleQty?.toString(),
                        ScaleQty: scale.scaleQty,
                        ScaleUOM: scaleUOM,
                        Price   : scale.price,
                        PriceUOM: priceUOM
                ]
            }
        }

        lineIds.collate(2000).each { linesIds ->
            for (quoteScaleRow in query.getQuoteScalesRows(linesIds)) {
                plItemAux = plItems.remove([quoteScaleRow.QuoteID, quoteScaleRow.LineID, quoteScaleRow.ScaleID])
                //Update existing Quote Scales item
                if (plItemAux) {

                    def result = target.addRow(plItemAux)
                } else { //Remove existing Quote Scales item
                    quoteScaleRow.isDeleted = true
                    target.addRow(quoteScaleRow)
                }
            }
        }

        //Add new Quote Scales items
        plItems.each {
            target.addRow(it.value)
        }
    }

}

libs.PricelistLib.Calculations.addOrUpdatePriceListForScalesStatusToReadyUsingBoundCall(pendingPriceListIds)

Map getQuoteIdAndLineId (quoteDSKeys) {
    List quoteDSKeysList = quoteDSKeys?.split("\\|")?.toList()
    return [
            QuoteId : quoteDSKeysList?.getAt(0),
            LineId  : quoteDSKeysList?.getAt(1)
    ]
}

List getScales (plItem) {
    def scaleQtyAux, priceAux
    def scales = []
    for (int i = 1; i < 6; i++) {
        scaleQtyAux = plItem["Scale Qty ${i}"]
        priceAux = plItem["Price ${i}"]
        if (scaleQtyAux && priceAux) {
            scales.add([
                    scaleQty: scaleQtyAux,
                    price   : priceAux
            ])
        }
    }

    return scales
}

def findPricelistTypes(List pendingPriceListIds) {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("ApprovedPriceListStatus")

    return qapi.source(t1, [t1.key1(), t1.Type], t1.key1().in(pendingPriceListIds)).stream {
        it.collectEntries { [(it.key1): it.Type] }
    }
}

def getQuoteIdAndLineIdFromQuotesDS(List filteredItems) {
    Set<String> contractNumbers = new HashSet<>()
    Set<String> contractLines = new HashSet<>()
    for (plItem in filteredItems) {
        if (plItem["Contract #"]) contractNumbers.add(plItem["Contract #"])
        if (plItem["Contract Line"]) contractLines.add(plItem["Contract Line"])
    }

    def keyFilters = []
    if (contractNumbers) {
        keyFilters.add(Filter.in("SAPContractNumber", contractNumbers))
    }
    if (contractLines) {
        keyFilters.add(Filter.in("SAPLineID", contractLines))
    }

    if (!keyFilters) return [:]

    def filters = Filter.and(*keyFilters)

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def query = ctx.newQuery(ds, false)
    query = query
            .select("SAPContractNumber", "SAPContractNumber")
            .select("SAPLineID", "SAPLineID")
            .select("QuoteID", "QuoteId")
            .select("LineID", "LineId")
            .setUseCache(false)
            .where(filters)
            .orderBy("QuoteLastUpdate ASC")

    return ctx.executeQuery(query)?.getData()?.toList()?.collectEntries {
        [(it.SAPContractNumber + "|" + it.SAPLineID): it]
    }
}