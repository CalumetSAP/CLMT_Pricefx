import java.text.SimpleDateFormat

api.global.isFirstRow = api.global.isFirstRow == null

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("582")

String material = api.product("sku")
api.local.material = material
api.local.secondaryKey = !api.isDebugMode() ? api.getSecondaryKey() : "US30"
api.local.currentContext = api.currentContext(material, api.local.secondaryKey)
api.local.salesOrg = api.local.secondaryKey

if (api.global.isFirstRow) {
    api.global.pricelistId = !api.isDebugMode() ? api.currentItem("pricelistId") : "498"
    api.global.effectiveDate = input?.Inputs?.EffectiveDateInput
    api.global.expirationDate = input?.Inputs?.ExpirationDateInput
    api.global.priceChangePercent = input?.Inputs?.PriceChangePercentInput?.toBigDecimal()
    api.global.priceChangePerUOM = input?.Inputs?.PriceChangePerUOMInput?.toBigDecimal()
    api.global.uom = input?.Inputs?.UOMInput

    api.global.salesOrgs = out.IsOnlyOneRow ? [api.local.salesOrg] : input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
}

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
if (api.getManualOverride("NewEffectiveDate")) {
    api.local.newEffectiveDate = sdf.parse(api.getManualOverride("NewEffectiveDate"))
} else {
    api.local.newEffectiveDate = sdf.parse(api.global.effectiveDate)
}

return null