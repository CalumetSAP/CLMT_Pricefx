import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) api.abortCalculation()

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("575")

api.global.isFirstRow = api.global.isFirstRow == null

if (api.global.isFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
}


api.local.sku = !api.isDebugMode() ? api.product("sku") : "300161125002"
