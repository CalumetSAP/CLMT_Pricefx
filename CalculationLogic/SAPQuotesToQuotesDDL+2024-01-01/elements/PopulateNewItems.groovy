if (api.isInputGenerationExecution()) return

def quoteId = api.isDebugMode() ? "1712.Q" : dist?.calcItem?.Key2

loader = api.isDebugMode() ? [] : dist.dataLoader

List lineIds = api.getCalculableLineItemCollection(quoteId)?.lineItems?.lineId ?: []

Map quotesDSRows = getQuotesDSRows(lineIds)
Map sapQuotesDSRows = getSAPQuotesDSRows(lineIds)

def sapQuoteRow, sapContractNumber, quoteRow
for (lineId in lineIds) {
    quoteRow = quotesDSRows.get(lineId)
    if (!quoteRow) continue

    sapQuoteRow = sapQuotesDSRows.get(lineId)
    sapContractNumber = sapQuoteRow?.SAPContractNumber
    if (!sapContractNumber) continue

    quoteRow.SAPContractNumber = sapContractNumber
    quoteRow.SAPLineID = sapQuoteRow?.SAPLineID
    loader.addRow(quoteRow)
}
//TODO talk about new SAP items? With QuoteID and UpdatedbyID should I use?

api.trace("loader", loader)

def getQuotesDSRows (lineIds) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("Quotes")

    def filters = [
            Filter.in("LineID", lineIds),
            Filter.or(
                    Filter.equal("SAPContractNumber", ""),
                    Filter.isNull("SAPContractNumber")
            )
    ]

    def query = ctx.newQuery(dm, false)
            .selectAll(true)
            .where(*filters)

    return ctx.executeQuery(query)?.getData()?.collectEntries {
        [(it.LineID): it]
    } ?: [:]
}

def getSAPQuotesDSRows (lineIds) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("SAPQuotes")

    def query = ctx.newQuery(dm, false)
            .select("LineID", "LineID")
            .select("SAPContractNumber", "SAPContractNumber")
            .select("SAPLineID", "SAPLineID")
            .where(Filter.in("LineID", lineIds))

    return ctx.executeQuery(query)?.getData()?.collectEntries {
        [(it.LineID): it]
    } ?: [:]
}
