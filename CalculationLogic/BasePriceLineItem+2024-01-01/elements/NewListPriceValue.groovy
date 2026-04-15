def newPrice
if(out.CurrentListPrice?.toBigDecimal()){
    if(api.local.priceChangePercent){
        newPrice = out.CurrentListPrice?.toBigDecimal() * (1 + api.local.priceChangePercent)
    }else if(api.local.priceChangePerUOM){
        def priceChange = api.local.priceChangePerUOM?.toBigDecimal()
        def pricingUOM = out.UOM
        def selectedUOM = api.local.uom
        def currPrice = out.CurrentListPrice?.toBigDecimal()
        if(priceChange && selectedUOM && pricingUOM && selectedUOM != pricingUOM){//convert priceChange from selectedUOM to pricingUOM
            def conversionFactor = libs.PricelistLib.Conversion.getConversionFactor(api.local.material, selectedUOM, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()
            priceChange = conversionFactor ? priceChange * conversionFactor : null
        }
        newPrice = priceChange && currPrice ? currPrice + priceChange : null
    }
}

return newPrice