if (!quoteProcessor.isPostPhase()) return
if (!out.FindZDGS) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def zdgsData = out.FindZDGS

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_ZDGS_SCALES)

def customFilter = Filter.and(
        Filter.in("CondRecNo", zdgsData.CondRecNo)
)

def query = ctx.newQuery(dm, false)
        .select("CondRecNo", "CondRecNo")
        .select("ScaleQuantity", "ScaleQuantity")
        .select("ConditionRate", "ConditionRate")
        .where(customFilter)

def result = ctx.executeQuery(query)
def data = result?.getData()
def zdgsScalesMap = [:]

data?.each {
    if (!zdgsScalesMap.containsKey(it.CondRecNo)) zdgsScalesMap[it.CondRecNo] = []
    zdgsScalesMap[it.CondRecNo].add(it)
}

return zdgsScalesMap
