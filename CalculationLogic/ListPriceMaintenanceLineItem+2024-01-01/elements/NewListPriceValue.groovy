//This if is to avoid unnecessary calculation. "Fail Fast"
def newListPriceZLISOverride = api.local.newListPriceZLISOverride
if (newListPriceZLISOverride) return null

if (api.local.isNewProduct) return api.local.zlisPrice

//TODO move to a PricelistLib. DUPLICATED (3)
BigDecimal newPrice = null
if (out.CurrentListPrice?.toBigDecimal()) {
    if (out.IsPriceChangePerUOM) {
        BigDecimal currPrice = out.CurrentListPrice?.toBigDecimal()
        BigDecimal priceChange = out.ListPriceChangePerUOM?.toBigDecimal()
        if (priceChange == BigDecimal.ZERO) {
            newPrice = currPrice
        } else {
            String pricingUOM = out.PriceUOM
            String selectedUOM = out.ListPriceChangeUOM
            if (priceChange && selectedUOM && pricingUOM && selectedUOM != pricingUOM) {//convert priceChange from selectedUOM to pricingUOM
                String material = api.local.material
                BigDecimal conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(material, selectedUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
                if (conversionFactor) {
                    priceChange = priceChange * conversionFactor
                } else {
                    api.local.newListPriceError = "Missing conversion from Input UOM (${selectedUOM}) to ZLIS UOM (${pricingUOM}) for material ${material}"
                    priceChange = null
                }
            }
            newPrice = priceChange && currPrice ? currPrice + priceChange : null
        }
    } else {
        BigDecimal currPrice = out.CurrentListPrice?.toBigDecimal()
        BigDecimal priceChangePercent = out.ListPriceChangePercent?.toBigDecimal()

        newPrice = currPrice != null && priceChangePercent != null ? currPrice * (1 + priceChangePercent) : null
    }
}

return newPrice
