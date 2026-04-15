api.global.isFirstRow = api.global.isFirstRow == null

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("161")

api.local.material = api.product("sku")
api.local.secondaryKey = api.getSecondaryKey()

api.local.effectiveDate = input?.Inputs?.EffectiveDateInput
api.local.expirationDate = input?.Inputs?.ExpirationDateInput
api.local.priceChangePercent = input?.Inputs?.PriceChangePercentInput?.toBigDecimal()
api.local.priceChangePerUOM = input?.Inputs?.PriceChangePerUOMInput?.toBigDecimal()
api.local.uom = input?.Inputs?.UOMInput
api.local.jobberPercent = input?.Inputs?.JobberPercentInput?.toBigDecimal()
api.local.srpPercent = input?.Inputs?.SRPPercentInput?.toBigDecimal()
api.local.mapPercent = input?.Inputs?.MapPercentInput?.toBigDecimal()
api.local.pricelists = input?.Inputs?.PricelistInput?.collect { it.trim().split(" ")?.first() }

api.local.secondaryKey = !api.isDebugMode() ? api.getSecondaryKey() : "08"
api.local.pricelist = api.local.secondaryKey.split('-')?.first()

return null