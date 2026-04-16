def ph1 = out.PH1?.getFirstInput()?.getValue()
def ph2 = out.PH2?.getFirstInput()?.getValue()
def ph3 = out.PH3?.getFirstInput()?.getValue()

def ph4 = out.FindProductHierarchy?.findAll{it.attribute1 == "4"}

if(ph1) ph4 = ph4.findAll{(it.sku as String).substring(0, 2) in ph1}
if(ph2) ph4 = ph4.findAll{(it.sku as String).substring(0, 4) in ph2}
if(ph3) ph4 = ph4.findAll{(it.sku as String).substring(0, 7) in ph3}

ph4 = ph4?.collect{it.sku}

//api.jsonDecode(api.global.selectedVariant?.attributeExtension___Products)
def variantProductHierarchies = api.global.selectedVariant?.PH4

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_4_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PRODUCT_HIERARCHY_4_INPUT_LABEL,
        false,
        false,
        null,
        ph4
)

if((api.global.variantChanged && variantProductHierarchies) || (!entry.getFirstInput().getValue() && variantProductHierarchies)) {
    entry.getFirstInput().setValue(variantProductHierarchies?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry