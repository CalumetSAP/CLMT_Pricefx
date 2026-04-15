if(api.global.PricingMap?.get(api.local.material)?.get(api.local.pricelist)){//is on Pricing DS
   return api.global.PricingMap?.get(api.local.material)?.get(api.local.pricelist)?.UOM
}

//Available list of UOM's based on the material
def options = [api.global.products?.get(api.local.material)?.UOM]

def crossedUom = api.global.uomConversion?.availableUom?.get(api.local.material)*.aUn ?: []
options << crossedUom

return api.attributedResult()
        .withManualOverrideValueOptions(options?.flatten()?.unique())
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())