if (api.isInputGenerationExecution()) api.abortCalculation()

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
//    def customFilter = Filter.and(*filters)
//
//    def ctx = api.getDatamartContext()
//    def ds = ctx.getDataSource("Quotes")
//
//    def query = ctx.newQuery(ds)
//            .select("Material", "Material")
//            .select("SAPContractNumber", "SAPContractNumber")
//            .select("SAPLineID", "SAPLineID")
//            .select("SoldTo", "SoldTo")
//            .select("ShipTo", "ShipTo")
//            .setUseCache(false)
//            .where(customFilter)
//
//    def result = ctx.executeQuery(query)
//
//    api.global.quotes = result?.getData()
//    api.global.groupedQuotes = api.global.quotes?.groupBy { it.Material }?.collectEntries { material, records ->
//        [material, records]
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
            .select("Material", "Material")
            .select("SAPContractNumber", "SAPContractNumber")
            .select("SAPLineID", "SAPLineID")
            .select("SoldTo", "SoldTo")
            .select("ShipTo", "ShipTo")
            .select("PriceType", "PriceType")
            .select("ContractEffectiveDate", "ContractEffectiveDate")
            .select("ContractExpiryDate", "ContractExpiryDate")
            .select("IndexIndicator", "IndexIndicator")
            .select("RejectionReason", "RejectionReason")
            .select("SalesOrg", "SalesOrg")
            .select("QuoteLastUpdate", "QuoteLastUpdate")
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
        [material, records]
    }
}

return null