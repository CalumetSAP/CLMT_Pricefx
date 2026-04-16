def getValidIndexQuotesRowsOld (Set<String> fields, Date calculationDate, List<String> recalculationPeriods = null, List<String> referencePeriods = null, List<String> indexNumbers = null) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(calculationDate)
    Integer day = calendar.get(Calendar.DAY_OF_MONTH)
    Integer month = calendar.get(Calendar.MONTH)+1 //+1 because the month are from 0 to 11

    def customFilters = [
            Filter.equal("PriceType", "1"),
            Filter.lessOrEqual("ContractEffectiveDate", calculationDate),
            Filter.greaterOrEqual("ContractExpiryDate", calculationDate),
            Filter.equal("RecalculationDate", day),
            Filter.isNotEmpty("SAPContractNumber"),
            Filter.isNotNull("SAPContractNumber"),
            Filter.notEqual("SAPContractNumber", ""),
            Filter.isNotEmpty("SAPLineID"),
            Filter.isNotNull("SAPLineID"),
            Filter.notEqual("SAPLineID", ""),
            Filter.or(
                    Filter.isNull("RejectionReason"),
                    Filter.isEmpty("RejectionReason"),
                    Filter.equal("RejectionReason", "")
            )
    ]
    if (![1, 4, 7, 10].contains(month)) { //If is not the first month of any quarter only bring Monthly recalculations
        customFilters << Filter.equal("RecalculationPeriod", "Month")
    } else if (recalculationPeriods && recalculationPeriods.size() == 1) { //If it is different that 1, it means that the query should search for all periods (Month and Quarter)
        customFilters << Filter.equal("RecalculationPeriod", recalculationPeriods.find())
    }
    if (referencePeriods) {
        customFilters << Filter.in("ReferencePeriod", referencePeriods)
    }
    if (indexNumbers) {
        customFilters << Filter.or(
                Filter.in("IndexNumberOne", indexNumbers),
                Filter.in("IndexNumberTwo", indexNumbers),
                Filter.in("IndexNumberThree", indexNumbers),
        )
    }

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")

    def query = ctx.newQuery(ds, false)

    fields.add("QuoteLastUpdate")
    fields.each {field ->
        query = query.select(field, field)
    }

    query = query
            .where(*customFilters)
            .setUseCache(false)
            .orderBy("QuoteLastUpdate DESC")

    return ctx.executeQuery(query)?.getData()
}

def getValidIndexQuotesRows(Set<String> fields, Date calculationDate, List<String> recalculationPeriods = null, List<String> referencePeriods = null, List<String> indexNumbers = null) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(calculationDate)
    Integer day = calendar.get(Calendar.DAY_OF_MONTH)
    Integer month = calendar.get(Calendar.MONTH) + 1

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("Quotes")
    def query = ctx.newQuery(ds, false)

    fields.addAll(["QuoteLastUpdate", "SAPContractNumber", "SAPLineID", "PriceType", "ContractEffectiveDate",
                   "ContractExpiryDate", "RecalculationDate", "SAPContractNumber", "SAPLineID",
                   "RejectionReason", "RecalculationPeriod", "ReferencePeriod",
                   "IndexNumberOne", "IndexNumberTwo", "IndexNumberThree"])
    fields.each { field ->
        query = query.select(field, field)
    }

    def customFilters = [
            Filter.lessOrEqual("ContractEffectiveDate", calculationDate),
            Filter.greaterOrEqual("ContractExpiryDate", calculationDate),
            Filter.notEqual("RejectionFlag", true)
    ]

    query = query
            .where(*customFilters)
            .setUseCache(false)
            .orderBy("QuoteLastUpdate DESC")

    def allRows = ctx.executeQuery(query)?.getData() ?: []

    def groupedRows = allRows.groupBy { row ->
        [row["SAPContractNumber"], row["SAPLineID"]]
    }.collect { key, group ->
        group.max { it["QuoteLastUpdate"] }
    }

    def filteredRows = groupedRows.findAll { row ->
        row["PriceType"] == "1" &&
                row["ContractEffectiveDate"] <= calculationDate &&
                row["ContractExpiryDate"] >= calculationDate &&
                row["RecalculationDate"] == day &&
                row["SAPContractNumber"] &&
                row["SAPContractNumber"].trim() &&
                row["SAPLineID"] &&
                row["SAPLineID"].trim() &&
                (!row["RejectionReason"] || row["RejectionReason"].trim() == "")
    }

    if (![1, 4, 7, 10].contains(month)) { //If is not the first month of any quarter only bring Monthly recalculations
        filteredRows = filteredRows.findAll { row ->
            row["RecalculationPeriod"] == "Month"
        }
    } else if (recalculationPeriods && recalculationPeriods.size() == 1) { //If it is different that 1, it means that the query should search for all periods (Month and Quarter)
        filteredRows = filteredRows.findAll { row ->
            row["RecalculationPeriod"] == recalculationPeriods.find()
        }
    }

    if (referencePeriods) {
        filteredRows = filteredRows.findAll { row ->
            row["ReferencePeriod"] in referencePeriods
        }
    }

    if (indexNumbers) {
        filteredRows = filteredRows.findAll { row ->
            row["IndexNumberOne"] in indexNumbers ||
                    row["IndexNumberTwo"] in indexNumbers ||
                    row["IndexNumberThree"] in indexNumbers
        }
    }

    return filteredRows
}
