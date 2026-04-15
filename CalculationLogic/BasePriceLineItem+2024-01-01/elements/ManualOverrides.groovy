if(api.global.PricingMap?.get(api.local.material)?.get(api.local.pricelist)){ // the item comes from Pricing DS
    api.removeManualOverride("UOM")
    api.removeManualOverride("EffectiveDate")
    api.removeManualOverride("ExpirationDate")
}

if(!(api.currentItem("pricelistId") && api.global.currentBatch?.size() == 1)){
    api.removeManualOverride("NewListPrice")
}
