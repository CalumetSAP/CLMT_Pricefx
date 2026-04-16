import java.text.SimpleDateFormat

if (libs.SharedLib.BatchUtils.isNewBatch() && api.global.batchKeys2) {
    def secondaryKeys = api.global.batchKeys2 as List
    def contractNumbers = secondaryKeys?.collect { it.split("-")[0] }?.toSet()?.toList()
    def contractLines = secondaryKeys?.collect { it.split("-")[1] }?.toSet()?.toList()

    if (!contractNumbers || !contractLines) {
        api.global.quotes = [:]
        return
    }

    final tableConstants = libs.QuoteConstantsLibrary.Tables

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy")

    def ctx = api.getDatamartContext()
    def dataSource= ctx.getDataSource(tableConstants.DATA_SOURCE_QUOTES)
    def query = ctx.newQuery(dataSource, false)

    query.identity {
        select("SAPContractNumber", "SAPContractNumber")
        select("SAPLineID", "SAPLineID")
        select("LineID", "LineID")
        select("SoldTo", "SoldTo")
        select("ShipTo", "ShipTo")
        select("SoldtoName", "SoldtoName")
        select("ShiptoName", "ShiptoName")
        select("ShippingPoint", "ShippingPoint")
        select("Material", "Material")
        select("ContractEffectiveDate", "ContractEffectiveDate")
        select("ModeOfTransportation", "ModeOfTransportation")
        select("MeansOfTransportation", "MeansOfTransportation")
        select("SalesOrg", "SalesOrg")
        select("Division", "Division")
        select("MOQ", "MOQ")
        select("MOQUOM", "MOQUOM")
        select("Plant", "Plant")
        select("ShippingPoint", "ShippingPoint")
        select("PriceType", "PriceType")
        select("Price", "Price")
        select("PriceValidFrom", "PriceValidFrom")
        select("PriceValidTo", "PriceValidTo")
        select("Currency", "Currency")
        select("NumberofDecimals", "NumberOfDecimals")
        select("PricingUOM", "PricingUOM")
        select("FreightAmount", "FreightAmount")
        select("FreightUOM", "FreightUOM")
        select("DeliveredPrice", "DeliveredPrice")
        select("FreightValidFrom", "FreightValidFrom")
        select("FreightValidto", "FreightValidTo")
        select("Adder", "Adder")
        select("AdderUOM", "AdderUOM")
        select("RecalculationPeriod", "RecalculationPeriod")
        select("RecalculationDate", "RecalculationDate")
        select("FreightTerm", "FreightTerm")
        select("MovementTiming", "MovementTiming")
        select("MovementStart", "MovementStart")
        select("MovementDay", "MovementDay")
        select("PriceProtection", "PriceProtection")
        select("NoOfDays", "NoOfDays")
        select("QuoteLastUpdate", "QuoteLastUpdate")
        where(Filter.in("SAPContractNumber", contractNumbers))
        where(Filter.in("SAPLineID", contractLines))
        where(Filter.isNotNull("PriceValidFrom"))
        orderBy("QuoteLastUpdate DESC")
    }

    def rows = ctx.executeQuery(query).getData().toResultMatrix().getEntries() ?: []

    def latestByContractLine = [:]

    rows.each { r ->
        def k = r.SAPContractNumber + "|" + r.SAPLineID + "|" + sdf.format(r.PriceValidFrom)
        def current = latestByContractLine[k]

        if (!current) {
            latestByContractLine[k] = r
        } else {
            def currDate = current.QuoteLastUpdate
            def newDate  = r.QuoteLastUpdate
            if (newDate && (!currDate || newDate > currDate)) {
                latestByContractLine[k] = r
            }
        }
    }

    def values = latestByContractLine.values() as List

    api.global.lineIds = values.collect { it.LineID }.toSet().toList()
    api.global.shipToList = values.collect { it.ShipTo }.toSet().toList()
    api.global.plantList = values.collect { it.Plant }.toSet().toList()

    api.global.quotes = latestByContractLine ?: [:]
}

return api.global.quotes[api.local.contractNumber + "|" + api.local.contractLine + "|" + api.local.validFrom] ?: [:]