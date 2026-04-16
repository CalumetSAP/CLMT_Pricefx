import java.text.SimpleDateFormat

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def combinationKeys = api.global.matrixContractCombinationKeys as List ?: []
    def conditionRecordsFound = api.global.matrixConditionRecordsFound as List ?: []
    def effectiveDate = api.global.effectiveDate

    final tableConstants = libs.QuoteConstantsLibrary.Tables

    def ctx = api.getDatamartContext()
    def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
    def query = ctx.newQuery(dataSource, false)

    query.identity {
        select("SAPContractNumber", "SAPContractNumber")
        select("SAPLineID", "SAPLineID")
        select("SoldTo", "SoldTo")
        select("ShipTo", "ShipTo")
        select("SoldtoName", "SoldtoName")
        select("ShiptoName", "ShiptoName")
        select("ShippingPoint", "ShippingPoint")
        select("Material", "Material")
        select("PriceValidFrom", "PriceValidFrom")
        select("PriceValidTo", "PriceValidTo")
        select("ModeOfTransportation", "ModeOfTransportation")
        select("MeansOfTransportation", "MeansOfTransportation")
        select("SalesOrg", "SalesOrg")
        select("Division", "Division")
        select("PriceType", "PriceType")
        select("FreightTerm", "FreightTerm")
        select("FreightAmount", "FreightAmount")
        select("FreightUOM", "FreightUOM")
        select("FreightValidFrom", "FreightValidFrom")
        select("FreightValidto", "FreightValidto")
        select("RejectionFlag", "RejectionFlag")
        select("QuoteLastUpdate", "QuoteLastUpdate")
        where(Filter.isNotNull("PriceValidFrom"))
        if (api.global.batchKeys1) where(Filter.in("Material", api.global.batchKeys1))
        if (api.global.salesOrgs) where(Filter.in("SalesOrg", api.global.salesOrgs))
        if (api.global.divisions) where(Filter.in("Division", api.global.divisions))

        orderBy("QuoteLastUpdate DESC")
    }

    def rows = ctx.executeQuery(query).getData().toResultMatrix().getEntries() ?: []

    def latestActiveByContractLine = [:]
    def latestFutureByContractLine = [:]

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy")

    def k, k2, current, currDate, newDate
    rows.each { r ->
        k = r.SAPContractNumber + "|" + r.SAPLineID + "|" + (r.FreightValidFrom ? sdf.format(r.FreightValidFrom) : "")
        if (combinationKeys.contains(k)) return

        k2 = r.SAPContractNumber + "|" + r.SAPLineID
        if (r.PriceValidFrom <= effectiveDate && r.PriceValidTo >= effectiveDate) {
            if (conditionRecordsFound.contains(k2 + "|CURRENT")) return
            current = latestActiveByContractLine[k2]

            if (!current) {
                latestActiveByContractLine[k2] = r
            } else {
                currDate = current.QuoteLastUpdate
                newDate  = r.QuoteLastUpdate
                if (newDate && (!currDate || newDate > currDate)) {
                    latestActiveByContractLine[k2] = r
                }
            }
        } else if (r.PriceValidFrom > effectiveDate && r.PriceValidTo >= effectiveDate) {
            if (conditionRecordsFound.contains(k2 + "|FUTURE")) return
            current = latestFutureByContractLine[k2]

            if (!current) {
                latestFutureByContractLine[k2] = r
            } else {
                currDate = current.QuoteLastUpdate
                newDate  = r.QuoteLastUpdate
                if (newDate && (!currDate || newDate > currDate)) {
                    latestFutureByContractLine[k2] = r
                }
            }
        }

    }

    def activeTempRows = latestActiveByContractLine.values() as List
    def futureTempRows = latestFutureByContractLine.values() as List

    api.global.matrixActiveQuotesNotInCR = evalRows(activeTempRows, effectiveDate, api.global.modeOfTransportation, api.global.meansOfTransportation as List)
    api.global.matrixFutureQuotesNotInCR = evalRows(futureTempRows, effectiveDate, api.global.modeOfTransportation, api.global.meansOfTransportation as List)
}

return null

def evalRows(List tempRows, effectiveDate, modeOfTransportation, List meansOfTransportation) {
    tempRows = tempRows.findAll { row ->
        !row.RejectionFlag
    }

    tempRows = tempRows.findAll { row ->
        row.FreightAmount && row.FreightUOM && row.FreightValidFrom && row.FreightValidto
    }

    tempRows = tempRows.findAll { row ->
        row.FreightTerm && row.FreightTerm != "1"
    }

    if (effectiveDate) {
        tempRows = tempRows.findAll { row ->
            row.PriceValidTo >= effectiveDate && row.FreightValidto >= effectiveDate
        }
    }

    if (modeOfTransportation) {
        tempRows = tempRows.findAll { row ->
            row.ModeOfTransportation == modeOfTransportation
        }
    }

    if (meansOfTransportation) {
        tempRows = tempRows.findAll { row ->
            meansOfTransportation.contains(row.MeansOfTransportation)
        }
    }

    return tempRows.groupBy { it.Material }
}