import java.text.SimpleDateFormat

if (api.global.isFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("551")
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)
}

return null