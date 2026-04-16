import net.pricefx.domain.ProductGroup

import java.text.SimpleDateFormat

if (api.global.isFirstRow) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

    if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("316")

    api.global.effectiveDate = sdf.parse(input?.Inputs?.EffectiveDateInput)
    api.global.salesOrgs = input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
    api.global.pricelists = input?.Inputs?.PricelistInput?.collect { it.trim().split(" ")?.first() } ?: []

    ProductGroup productGroup = input?.Inputs?.ProductsInput as ProductGroup
    api.global.products = !productGroup?.label ? [] : api.getSkusFromProductGroup(productGroup) as List
    api.global.pricelistsNewItems = input?.Inputs?.PricelistNewItemsInput?.collect { it.trim().split(" ")?.first() } ?: []
}

return null