if(api.global.PricingMap?.get(api.local.material)?.get(api.local.pricelist)){//is not on Pricing DS
    return api.local.effectiveDate
}

return api.attributedResult(api.local.effectiveDate).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())