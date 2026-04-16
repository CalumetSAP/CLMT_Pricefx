def ph1 = out.PH1?.getFirstInput()?.getValue()
def ph2 = out.FindProductHierarchy?.findAll{it.attribute1 == "2"}

if(ph1) ph2 = ph2.findAll{(it.sku as String).substring(0, 2) in ph1}

ph2 = ph2?.collect{it.sku}

def variantProductHierarchies = api.global.selectedVariant?.PH2

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_2_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_2_INPUT_LABEL,
        false,
        false,
        null,
        ph2
)

if((api.global.variantChanged && variantProductHierarchies) || (!entry.getFirstInput().getValue() && variantProductHierarchies)) {
    entry.getFirstInput().setValue(variantProductHierarchies?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry