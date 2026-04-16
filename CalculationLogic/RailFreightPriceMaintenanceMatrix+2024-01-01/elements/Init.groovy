import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) api.abortCalculation()

api.global.isFirstRow = api.global.isFirstRow == null

if (api.global.isFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("792")
    api.global.conditionType = input?.Inputs?.ConditionTypeInput
    api.global.modeOfTransportation = input?.Inputs?.ModeOfTransportationInput
    api.global.meansOfTransportation = input?.Inputs?.MeansOfTransportationInput
    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)
    api.global.expirationDate = sdf.parse(input?.Inputs?.ExpirationDateInput)
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput
    api.global.divisions = input?.Inputs?.DivisionInput
}

api.local.sku = api.product("sku")