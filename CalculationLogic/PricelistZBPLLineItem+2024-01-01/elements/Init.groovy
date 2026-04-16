import net.pricefx.domain.ProductGroup
import java.text.SimpleDateFormat

api.global.isFirstRow = api.global.isFirstRow == null

if (api.isDebugMode()) input = libs.PricelistLib.Common.getInputsFromPricelistId("498")

String material = api.product("sku")
api.local.material = material
api.local.secondaryKey = !api.isDebugMode() ? api.getSecondaryKey() : "US30-04-1"
api.local.currentContext = api.currentContext(material, api.local.secondaryKey)

List secondaryKeySplitted = api.local.secondaryKey.split('-')
api.local.salesOrg = secondaryKeySplitted[0]
String pricelist = secondaryKeySplitted[1]
api.local.pricelist = pricelist

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
    if (!products.isEmpty()) {
        pricelists.addAll(input?.Inputs?.PricelistNewItemsInput?.collect { it.trim().split(" ")?.first() })
    }

    api.global.isFirstLoad = api.currentItem("pricelistId") == null
    api.global.pricelists = pricelists.unique()
    api.global.maxNumberOfDecimals = libs.PricelistLib.Constants.MAX_NUMBER_OF_DECIMALS_FOR_SCALES
}

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
if (api.getManualOverride("NewEffectiveDate")) {
    api.local.newEffectiveDate = sdf.parse(api.getManualOverride("NewEffectiveDate"))
} else {
    api.local.newEffectiveDate = sdf.parse(api.global.effectiveDate)
}

return null