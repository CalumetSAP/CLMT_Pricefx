if (api.isInputGenerationExecution()) api.abortCalculation()

if (libs.SharedLib.BatchUtils.isNewBatch()) {
    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def filters = [
            Filter.notEqual("RejectionFlag", true)
    ]

    def query = ctx.newQuery(ds, false)
            .select("Material", "Material")
            .select("SAPContractNumber", "SAPContractNumber")
            .select("SAPLineID", "SAPLineID")
            .select("SoldTo", "SoldTo")
            .select("ShipTo", "ShipTo")
            .select("PriceType", "PriceType")
            .select("PriceValidFrom", "PriceValidFrom")
            .select("PriceValidTo", "PriceValidTo")
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
                row["PriceValidFrom"] <= api.global.effectiveDate &&
                row["PriceValidTo"] >= api.global.effectiveDate &&
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