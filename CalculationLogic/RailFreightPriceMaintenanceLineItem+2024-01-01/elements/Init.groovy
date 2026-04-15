import java.text.SimpleDateFormat

def plId = api.isDebugMode() ? "864" : api.currentItem("pricelistId")

if (api.getIterationNumber() == 0 && !plId) {
    api.global.isFirstIterationFirstRow = api.global.isFirstIterationFirstRow == null
} else {
    api.global.isFirstRow = api.global.isFirstRow == null
}

if (api.global.isFirstRow || api.global.isFirstIterationFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("879")
    api.global.conditionType = input?.Inputs?.ConditionTypeInput
    api.global.modeOfTransportation = input?.Inputs?.ModeOfTransportationInput
    api.global.meansOfTransportation = input?.Inputs?.MeansOfTransportationInput
    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)
    api.global.expirationDate = sdf.parse(input?.Inputs?.ExpirationDateInput)
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput
    api.global.divisions = input?.Inputs?.DivisionInput
    api.global.kmToMilesConversionFactor = libs.PricelistLib.Conversion.getKMToMilesConversionFactor()

    api.global.pricelistId = plId
}

api.local.sku = api.product("sku")
def secondaryKey = api.isDebugMode() ? "40030810-10-105361-330874-05/02/2026" : api.getSecondaryKey()
api.local.secondaryKey = secondaryKey
api.local.contractNumber = secondaryKey.split("-")[0]
api.local.contractLine = secondaryKey.split("-")[1]
api.local.validFrom = secondaryKey.split("-")[4]
api.local.conversionAlerts = []