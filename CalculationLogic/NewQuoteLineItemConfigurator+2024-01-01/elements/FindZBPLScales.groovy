if (api.isInputGenerationExecution() || !out.FindBasePricing) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def condRecNo = out.FindBasePricing?.find()?.ConditionRecordNo

def ctx = api.getDatamartContext()
def dm = ctx.getDataSource(tablesConstants.DATA_SOURCE_ZBPL_SCALES)

def customFilter = Filter.equal("ConditionRecordNo", condRecNo)

def query = ctx.newQuery(dm, false)
        .select("ConditionRecordNo", "ConditionRecordNo")
        .select("ScaleQuantity", "ScaleQuantity")
        .select("ConditionRate", "ConditionRate")
        .where(customFilter)

def result = ctx.executeQuery(query)
return [(condRecNo): result?.getData()?.toList()]