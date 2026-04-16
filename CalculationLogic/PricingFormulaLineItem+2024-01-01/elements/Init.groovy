import java.text.SimpleDateFormat

api.global.isFirstRow = api.global.isFirstRow == null

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("457")

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

if (api.global.isFirstRow) {
    api.global.calculationDate = new SimpleDateFormat("yyyy-MM-dd").parse(input?.Inputs?.CalculationDateInput)
    api.global.recalculationPeriods = input?.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_RECALCULATION_PERIOD_ID)
    api.global.referencePeriods = input?.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_REFERENCE_PERIOD_ID)
    api.global.indexNumbers = input?.Inputs?.get(lineItemConstants.PF_CONFIGURATOR_INDEX_NUMBER_ID)
}

api.local.material = api.product("sku")
api.local.secondaryKey = !api.isDebugMode() ? api.getSecondaryKey() : "101792-307053-40014712-212"

return null