import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) api.abortCalculation()

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("108")

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

api.global.isFirstRow = api.global.isFirstRow == null

if (api.global.isFirstRow) {
    api.global.calculationDate = new SimpleDateFormat("yyyy-MM-dd").parse(input?.Inputs?.CalculationDateInput)
    api.global.recalculationPeriods = input?.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID)
    api.global.referencePeriods = input?.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
    api.global.indexNumbers = input?.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)
}

api.local.sku = !api.isDebugMode() ? api.product("sku") : "500920130433"