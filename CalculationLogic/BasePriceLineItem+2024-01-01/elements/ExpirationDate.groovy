if(api.global.PricingMap?.get(api.local.material)?.get(api.local.pricelist)){//is not on Pricing DS
    return api.local.expirationDate
}

return api.attributedResult(api.local.expirationDate).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())