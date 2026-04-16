if (libs.SharedLib.BatchUtils.isNewBatch()) {
//    def today = new Date()
//
//    def filters = [
//            Filter.lessOrEqual("ContractEffectiveDate", today),
//            Filter.greaterOrEqual("ContractExpiryDate", today),
//            Filter.equal("PriceType", "2"),
//            Filter.in("Material", api.global.currentBatch),
//            Filter.or(
//                    Filter.equal("IndexIndicator", false),
//                    Filter.isNull("IndexIndicator")
//            ),
//            Filter.isNotEmpty("SAPContractNumber"),
//            Filter.isNotNull("SAPContractNumber"),
//            Filter.notEqual("SAPContractNumber", ""),
//            Filter.isNotEmpty("SAPLineID"),
//            Filter.isNotNull("SAPLineID"),
//            Filter.notEqual("SAPLineID", ""),
//            Filter.or(
//                    Filter.isNull("RejectionReason"),
//                    Filter.isEmpty("RejectionReason"),
//                    Filter.equal("RejectionReason", "")
//            )
//    ]
//    if (api.global.salesOrgs) {
//        filters.add(Filter.in("SalesOrg", api.global.salesOrgs))
//    }
//
//    def ctx = api.getDatamartContext()
//    def ds = ctx.getDataSource("Quotes")
//
//    def query = ctx.newQuery(ds, false)
//            .select("QuoteID", "QuoteID")
//            .select("LineID", "LineID")
//            .select("UpdatedbyID", "UpdatedbyID")
//            .select("SalesOrg", "SalesOrg")
//            .select("Material", "Material")
//            .select("SAPContractNumber", "SAPContractNumber")
//            .select("SAPLineID", "SAPLineID")
//            .select("SoldTo", "SoldTo")
//            .select("ShipTo", "ShipTo")
//            .select("Plant", "Plant")
//            .select("Division", "Division")
//            .select("EffectiveDate", "EffectiveDate")
//            .select("ExpiryDate", "ExpiryDate")
//            .select("PricingUOM", "PricingUOM")
//            .select("Price", "Price")
//            .select("Per", "Per")
//            .select("NumberofDecimals", "NumberofDecimals")
//            .select("PriceValidFrom", "PriceValidFrom")
//            .select("PriceValidTo", "PriceValidTo")
//            .select("Currency", "Currency")
//            .select("QuoteLastUpdate", "QuoteLastUpdate")
//            .setUseCache(false)
//            .where(*filters)
//            .orderBy("QuoteLastUpdate DESC")
//
//    api.global.quotes = ctx.executeQuery(query)?.getData()
//    api.global.groupedQuotes = api.global.quotes?.groupBy { it.Material }?.collectEntries { material, records ->
//        [material, records?.groupBy{ (it.SoldTo?:"")+"-"+(it.ShipTo?:"")+"-"+(it.SAPContractNumber?:"")+"-"+(it.SAPLineID?:"") }]
//    }
    def today = new Date()

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def filters = [
            Filter.lessOrEqual("ContractEffectiveDate", today),
            Filter.greaterOrEqual("ContractExpiryDate", today),
            Filter.notEqual("RejectionFlag", true)
    ]

    def query = ctx.newQuery(ds, false)
            .select("QuoteID", "QuoteID")
            .select("LineID", "LineID")
            .select("UpdatedbyID", "UpdatedbyID")
            .select("SalesOrg", "SalesOrg")
            .select("Material", "Material")
            .select("SAPContractNumber", "SAPContractNumber")
            .select("SAPLineID", "SAPLineID")
            .select("SoldTo", "SoldTo")
            .select("ShipTo", "ShipTo")
            .select("PriceType", "PriceType")
            .select("Plant", "Plant")
            .select("Division", "Division")
            .select("EffectiveDate", "EffectiveDate")
            .select("ExpiryDate", "ExpiryDate")
            .select("PricingUOM", "PricingUOM")
            .select("Price", "Price")
            .select("Per", "Per")
            .select("NumberofDecimals", "NumberofDecimals")
            .select("PriceValidFrom", "PriceValidFrom")
            .select("PriceValidTo", "PriceValidTo")
            .select("Currency", "Currency")
            .select("QuoteLastUpdate", "QuoteLastUpdate")
            .select("IndexIndicator", "IndexIndicator")
            .select("ContractEffectiveDate", "ContractEffectiveDate")
            .select("ContractExpiryDate", "ContractExpiryDate")
            .select("RejectionReason", "RejectionReason")
            .setUseCache(false)
            .where(*filters)
            .orderBy("QuoteLastUpdate DESC")

    def allRows = ctx.executeQuery(query)?.getData() ?: []

    def groupedRows = allRows.groupBy { row ->
        [row["SAPContractNumber"], row["SAPLineID"]]
    }.collect { key, group ->
        group.max { it["QuoteLastUpdate"] }
    }

    def filteredRows = groupedRows.findAll { row ->
        row["PriceType"] == "2" &&
                row["ContractEffectiveDate"] <= today &&
                row["ContractExpiryDate"] >= today &&
                row["Material"] in api.global.currentBatch &&
                (!row["IndexIndicator"] || row["IndexIndicator"] == false) &&
                row["SAPContractNumber"] &&
                row["SAPContractNumber"].trim() &&
                row["SAPLineID"] &&
                row["SAPLineID"].trim() &&
                (!row["RejectionReason"] || row["RejectionReason"].trim() == "")
    }

    if (api.global.salesOrgs) {
        filteredRows = filteredRows.findAll { row ->
            row["SalesOrg"] in api.global.salesOrgs
        }
    }

    api.global.quotes = filteredRows
    api.global.groupedQuotes = api.global.quotes?.groupBy { it.Material }?.collectEntries { material, records ->
        [material, records?.groupBy {
            (it.SoldTo ?: "") + "-" +
                    (it.ShipTo ?: "") + "-" +
                    (it.SAPContractNumber ?: "") + "-" +
                    (it.SAPLineID ?: "")
        }]
    }
}

return api.global.groupedQuotes?.get(api.local.material)?.get(api.local.secondaryKey)?.first() ?: [:]