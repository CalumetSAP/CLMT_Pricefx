if (api.isInputGenerationExecution()) return

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("QuoteScales")

def query = ctx.newQuery(dm, false)
        .selectAll(true)
        .setUseCache(false)

api.global.quoteScales = ctx.executeQuery(query)?.getData()?.collect()?.groupBy { it.LineID } ?: []

return null