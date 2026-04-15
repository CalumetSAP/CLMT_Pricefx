if (api.isInputGenerationExecution()) return

if (api.global.zbpl) {
    final queryLib = libs.QuoteLibrary.Query

    Map zbplScales = [:]

    api.global.zbpl.values()?.ConditionRecordNo?.flatten()?.unique()?.collate(2000)?.each { conditionRecords ->
        zbplScales.putAll(queryLib.getZBPLScales(conditionRecords))
    }

    api.global.ZBPLScales = zbplScales
} else {
    api.global.ZBPLScales = [:]
}

return null