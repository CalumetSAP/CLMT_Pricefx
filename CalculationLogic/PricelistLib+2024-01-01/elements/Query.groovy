def getValidIndexQuotesRows (Set<String> fields, Date calculationDate, List<String> recalculationPeriods = null, List<String> referencePeriods = null, List<String> indexNumbers = null) {
    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def filters = [
            Filter.notEqual("RejectionFlag", true)
    ]

    def requiredFields = [
            "SAPContractNumber", "SAPLineID", "QuoteLastUpdate",
            "PriceType", "PriceValidFrom", "PriceValidTo",
            "ContractEffectiveDate", "ContractExpiryDate",
            "RecalculationDate", "RecalculationPeriod",
            "ReferencePeriod",
            "IndexNumberOne", "IndexNumberTwo", "IndexNumberThree"
    ] as Set

    (requiredFields + (fields ?: [] as Set)).each { f -> fields.add(f) }

    def query = ctx.newQuery(ds, false)
    fields.each { f -> query = query.select(f, f) }

    query = query
            .setUseCache(false)
            .where(*filters)
            .orderBy("QuoteLastUpdate DESC")

    def allRows = ctx.executeQuery(query)?.getData() ?: []

    def latestRows = allRows.groupBy { row ->
        [row["SAPContractNumber"], row["SAPLineID"]]
    }.collect { key, group ->
        group.max { it["QuoteLastUpdate"] }
    }

    Calendar cal = Calendar.getInstance()
    cal.setTime(calculationDate)
    Integer day = cal.get(Calendar.DAY_OF_MONTH)
    Integer month = cal.get(Calendar.MONTH) + 1

    def filteredRows = latestRows.findAll { row ->
        row["PriceType"] == "1" &&
                row["PriceValidFrom"] <= calculationDate &&
                row["PriceValidTo"] >= calculationDate &&
                row["RecalculationDate"] == day &&
                row["SAPContractNumber"]?.toString()?.trim() &&
                row["SAPLineID"]?.toString()?.trim()
    }

    if (![1, 4, 7, 10].contains(month)) {
        filteredRows = filteredRows.findAll { it["RecalculationPeriod"] == "Month" }
    } else if (recalculationPeriods && recalculationPeriods.size() == 1) {
        def period = recalculationPeriods.find()
        filteredRows = filteredRows.findAll { it["RecalculationPeriod"] == period }
    }

    if (referencePeriods) {
        def refSet = referencePeriods as Set
        filteredRows = filteredRows.findAll { it["ReferencePeriod"] in refSet }
    }

    if (indexNumbers) {
        def idxSet = indexNumbers as Set
        filteredRows = filteredRows.findAll {
            (it["IndexNumberOne"] in idxSet) ||
                    (it["IndexNumberTwo"] in idxSet) ||
                    (it["IndexNumberThree"] in idxSet)
        }
    }

    return filteredRows
}