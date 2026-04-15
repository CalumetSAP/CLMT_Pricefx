if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def conditionRecords = api.global.conditionRecords
    def contractNumbers = conditionRecords?.collect { it.ContractNr }?.toSet()?.toList()
    def contractLines = conditionRecords?.collect { it.ContractLine }?.toSet()?.toList()
    if (!contractNumbers || !contractLines) {
        api.global.quotes = [:]
        return
    }

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
        select("ModeOfTransportation", "ModeOfTransportation")
        select("MeansOfTransportation", "MeansOfTransportation")
        select("SalesOrg", "SalesOrg")
        select("Division", "Division")
        select("PriceType", "PriceType")
        select("FreightTerm", "FreightTerm")
        select("RejectionFlag", "RejectionFlag")
        select("QuoteLastUpdate", "QuoteLastUpdate")
        where(Filter.in("SAPContractNumber", contractNumbers))
        where(Filter.in("SAPLineID", contractLines))
        if (api.global.currentBatch) where(Filter.in("Material", api.global.currentBatch))
        orderBy("QuoteLastUpdate DESC")
    }

    def rows = ctx.executeQuery(query).getData().toResultMatrix().getEntries() ?: []

    def latestByContractLine = [:]

    def k, current, currDate, newDate
    rows.each { r ->
        k = r.SAPContractNumber + "|" + r.SAPLineID
        current = latestByContractLine[k]

        if (!current) {
            latestByContractLine[k] = r
        } else {
            currDate = current.QuoteLastUpdate
            newDate  = r.QuoteLastUpdate
            if (newDate && (!currDate || newDate > currDate)) {
                latestByContractLine[k] = r
            }
        }
    }

    def tempRows = latestByContractLine.values() as List

    tempRows = tempRows.findAll { row ->
        !row.RejectionFlag
    }

    if (api.global.modeOfTransportation) {
        tempRows = tempRows.findAll { row ->
            row.ModeOfTransportation == api.global.modeOfTransportation
        }
    }

    def meansOfTransportation = api.global.meansOfTransportation as List
    if (meansOfTransportation) {
        tempRows = tempRows.findAll { row ->
            meansOfTransportation.contains(row.MeansOfTransportation)
        }
    }

    def salesOrgs = api.global.salesOrgs as List
    if (salesOrgs) {
        tempRows = tempRows.findAll { row ->
            salesOrgs.contains(row.SalesOrg)
        }
    }

    def divisions = api.global.divisions as List
    if (divisions) {
        tempRows = tempRows.findAll { row ->
            divisions.contains(row.Division)
        }
    }

//    api.global.shipToList = tempRows.collect { it.ShipTo }.toSet().toList()
    api.global.quotes = tempRows.collectEntries { it ->
        [(it.SAPContractNumber + "|" + it.SAPLineID): it]
    }
}

return null