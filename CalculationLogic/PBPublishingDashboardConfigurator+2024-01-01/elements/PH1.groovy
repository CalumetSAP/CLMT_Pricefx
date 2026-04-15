def ph1 = out.FindProductHierarchy?.findAll{it.attribute1 == "1"}?.collect{it.sku}
def variantProductHierarchies = api.global.selectedVariant?.PH1

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_1_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_1_INPUT_LABEL,
        false,
        false,
        null,
        ph1
)
if((api.global.variantChanged && variantProductHierarchies) || (!entry.getFirstInput().getValue() && variantProductHierarchies)) {
    entry.getFirstInput().setValue(variantProductHierarchies?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry