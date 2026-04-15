import net.pricefx.domain.ProductGroup

import java.text.SimpleDateFormat

api.global.isFirstRow = api.global.isFirstRow == null

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("498")

String material = api.product("sku")
api.local.material = material
api.local.secondaryKey = !api.isDebugMode() ? api.getSecondaryKey() : "US30-04-1"
api.local.currentContext = api.currentContext(material, api.local.secondaryKey)

List secondaryKeySplitted = api.local.secondaryKey.split('-')
Integer splittedSize = secondaryKeySplitted?.size()
if (splittedSize == 1) { //Means that there is only ZLIS without ZBPL and scales
    api.local.salesOrg = secondaryKeySplitted[0]
} else if (splittedSize == 2) { //Means that there is ZBPL without scales
    api.local.salesOrg = secondaryKeySplitted[0]
    String pricelist = secondaryKeySplitted[1]
    api.local.pricelist = pricelist
} else if (splittedSize == 3) { //Means that there is ZBPL and scales
    api.local.salesOrg = secondaryKeySplitted[0]
    String pricelist = secondaryKeySplitted[1]
    api.local.pricelist = pricelist
    api.local.lineNumber = secondaryKeySplitted[2]
}
//Otherwise there is only ZLIS

if (api.global.isFirstRow) {
    api.global.pricelistId = !api.isDebugMode() ? api.currentItem("pricelistId") : "498"

    if (api.global.pricelistId && api.currentContext()?.commandName != "update") {
        def typedId = api.find("PL", 0, 1, null, Filter.equal("id", api.global.pricelistId))?.find()?.typedId
        def parameters = api.find("JST", 0, 1, "-lastUpdateDate", Filter.equal("targetObject", typedId))?.find()?.parameters
        api.global.isFullListRecalc = api.jsonDecode(parameters)?.fullListRecalc
    }

    api.global.effectiveDate = input?.Inputs?.EffectiveDateInput
    api.global.expirationDate = input?.Inputs?.ExpirationDateInput
    api.global.priceChangePercent = input?.Inputs?.PriceChangePercentInput?.toBigDecimal()
    api.global.priceChangePerUOM = input?.Inputs?.PriceChangePerUOMInput?.toBigDecimal()
    api.global.uom = input?.Inputs?.UOMInput

    api.global.salesOrgs = out.IsOnlyOneRow ? [api.local.salesOrg] : input?.Inputs?.SalesOrgInput?.collect { it.trim().split(" - ")?.first() }
    List pricelists = out.IsOnlyOneRow ? (api.local.pricelist ? [api.local.pricelist] : []) : input?.Inputs?.PricelistInput?.collect { it.trim().split(" ")?.first() }

    ProductGroup productGroup = input?.Inputs?.ProductsInput as ProductGroup
    List<String> products = !productGroup?.label ? [] : api.getSkusFromProductGroup(productGroup) as List
    api.global.newProducts = products
    if (!products.isEmpty()) {
        pricelists.addAll(input?.Inputs?.PricelistNewItemsInput?.collect { it.trim().split(" ")?.first() })
        Set<BigDecimal> scalesNewItems = [
                input?.Inputs?.Scale1Input,
                input?.Inputs?.Scale2Input,
                input?.Inputs?.Scale3Input,
                input?.Inputs?.Scale4Input,
                input?.Inputs?.Scale5Input,
        ]
        scalesNewItems.remove(null)
        if (!scalesNewItems.isEmpty()) {
            LinkedHashMap<String, BigDecimal> scalesMap = new LinkedHashMap()
            for (scale in scalesNewItems) {
                scalesMap[scale.toString()] = scale
            }
            LinkedHashMap<String, LinkedHashMap<String, BigDecimal>> productsAndScalesMap = new LinkedHashMap()
            for (product in products) {
                productsAndScalesMap[product] = scalesMap
            }
            api.global.productsAndScalesMap = productsAndScalesMap
        }
    }

    api.global.pricelists = pricelists.unique()
}

api.local.isNewProduct = api.global.newProducts?.contains(material)

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
if (api.getManualOverride("NewEffectiveDate")) {
    api.local.newEffectiveDate = sdf.parse(api.getManualOverride("NewEffectiveDate"))
} else {
    api.local.newEffectiveDate = sdf.parse(api.global.effectiveDate)
}

return null