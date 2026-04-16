if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() || !api.local.initCondRecNos) return

if (!api.local.addedContracts) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def condRecNoList = api.local.initCondRecNos as List

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_ZBPL_SCALES)

def customFilter = Filter.and(
        Filter.in("ConditionRecordNo", condRecNoList)
)

def query = ctx.newQuery(dm, false)
        .select("ConditionRecordNo", "ConditionRecordNo")
        .select("ScaleQuantity", "ScaleQuantity")
        .select("ConditionRate", "ConditionRate")
        .where(customFilter)

def result = ctx.executeQuery(query)
def data = result?.getData()
def zdgsScalesMap = [:]

data?.each {
    if (!zdgsScalesMap.containsKey(it.ConditionRecordNo)) zdgsScalesMap[it.ConditionRecordNo] = []
    zdgsScalesMap[it.ConditionRecordNo].add(it)
}

return zdgsScalesMap