if (api.isInputGenerationExecution()) return

def pendingUUIDs = api.global.pendingEmails?.values()?.flatten()?.collect { it.UUID } as List
if (!pendingUUIDs) return

def filter = Filter.in("UUID", pendingUUIDs)

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource("AutomatePriceLetters")

def query = ctx.newQuery(dm, false)
        .select("UUID", "UUID")
        .select("SalesPersonEmail", "SalesPersonEmail")
        .where(filter)
        .selectDistinct()

api.global.DSData = ctx.executeQuery(query)?.getData()?.groupBy {it.UUID } ?: [:]

return null