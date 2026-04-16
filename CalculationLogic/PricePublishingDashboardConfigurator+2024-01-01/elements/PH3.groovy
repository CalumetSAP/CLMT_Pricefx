def ph1 = out.PH1?.getFirstInput()?.getValue()
def ph2 = out.PH2?.getFirstInput()?.getValue()

def ph3 = out.FindProductHierarchy?.findAll{it.attribute1 == "3"}

if(ph1) ph3 = ph3.findAll{(it.sku as String).substring(0, 2) in ph1}
if(ph2) ph3 = ph3.findAll{(it.sku as String).substring(0, 4) in ph2}

ph3 = ph3?.collect{it.sku}

def variantProductHierarchies = api.global.selectedVariant?.PH3

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_3_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_3_INPUT_LABEL,
        false,
        false,
        null,
        ph3
)

if((api.global.variantChanged && variantProductHierarchies) || (!entry.getFirstInput().getValue() && variantProductHierarchies)) {
    entry.getFirstInput().setValue(variantProductHierarchies?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry