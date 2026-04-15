import net.pricefx.domain.ProductGroup

if (api.isInputGenerationExecution()) api.abortCalculation()

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("156")

api.global.isFirstRow = api.global.isFirstRow == null

api.local.effectiveDate = input?.Inputs?.EffectiveDateInput
api.local.expirationDate = input?.Inputs?.ExpirationDateInput
api.local.priceChangePercent = input?.Inputs?.PriceChangePercentInput?.toBigDecimal()
api.local.priceChangePerUOM = input?.Inputs?.PriceChangePerUOMInput?.toBigDecimal()
api.local.jobberPercent = input?.Inputs?.JobberPercentInput?.toBigDecimal()
api.local.srpPercent = input?.Inputs?.SRPPercentInput?.toBigDecimal()
api.local.mapPercent = input?.Inputs?.MapPercentInput?.toBigDecimal()
api.local.pricelist = input?.Inputs?.PricelistInput?.collect { it.trim().split(" ")?.first() }
ProductGroup productGroup = input?.Inputs?.ProductsInput as ProductGroup
api.local.products = !productGroup?.label ? [] : api.getSkusFromProductGroup(productGroup) as List

api.local.sku = api.product("sku")
