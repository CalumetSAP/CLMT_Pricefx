import net.pricefx.domain.ProductGroup

import java.text.SimpleDateFormat

if (api.global.isFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

    if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("316")

    api.global.pricelists = input?.Inputs?.PricelistInput?.collect { it.trim().split(" ")?.first() }
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)

    ProductGroup productGroup = input?.Inputs?.ProductsInput as ProductGroup
    api.global.products = !productGroup?.label ? [] : api.getSkusFromProductGroup(productGroup) as List
    if (!api.global.products.isEmpty()) {
        api.global.pricelistsNewItems = input?.Inputs?.PricelistNewItemsInput?.collect { it.trim().split(" ")?.first() }
        Set<BigDecimal> scalesNewItems = [
                input?.Inputs?.Scale1Input,
                input?.Inputs?.Scale2Input,
                input?.Inputs?.Scale3Input,
                input?.Inputs?.Scale4Input,
                input?.Inputs?.Scale5Input,
        ]
        scalesNewItems.remove(null)
        api.global.scalesNewItems = scalesNewItems
    } else {
        api.global.pricelistsNewItems = []
        api.global.scalesNewItems = []
    }
}

return null