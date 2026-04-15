if (api.isInputGenerationExecution()) return

def minutesToWait = api.findLookupTableValues("BusinessRules", ["attribute2"], null, Filter.equal("name", "MinutesAfterSendToSAP"))
        ?.find()?.attribute2?.toInteger() * -1

Calendar calendar = Calendar.getInstance()
calendar.add(Calendar.MINUTE, minutesToWait)

def filter = Filter.lessOrEqual("lastUpdateDate", calendar.getTime())
api.local.lastUpdateDateFilter = filter
//api.trace(filter)
api.local.pendingQuotes = libs.QuoteLibrary.Calculations.getPendingCustomEventRows(filter)

return