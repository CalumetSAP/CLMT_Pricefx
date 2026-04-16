import java.text.SimpleDateFormat

api.global.isFirstRow = api.global.isFirstRow == null

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("330")

if (api.global.isFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)
    api.global.expirationDate = sdf.parse(input?.Inputs?.ExpirationDateInput)
    api.global.announcementDate = sdf.parse(input?.Inputs?.AnnouncementDateInput)
    api.global.priceLetterDate = sdf.parse(input?.Inputs?.PriceLetterDateInput)
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
    api.global.priceChangePercent = input?.Inputs?.PriceChangePercentInput?.toBigDecimal()
    api.global.priceChangePerUOM = input?.Inputs?.PriceChangePerUOMInput?.toBigDecimal()
    api.global.uom = input?.Inputs?.UOMInput

    try {
        api.global.ccyExchangeRateUSDToEUR = libs.SharedLib.CurrencyUtils.getExchangeRate("USD", "EUR")
    } catch (ignored) {}
}

api.local.material = api.product("sku")
api.local.secondaryKey = !api.isDebugMode() ? api.getSecondaryKey() : "101792-305150-40022315-"

return null