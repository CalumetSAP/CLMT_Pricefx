String variantLabel = api.global.selectedVariant?.Label

def entry = libs.BdpLib.UserInputs.createInputString(
        libs.DashboardConstantsLibrary.PricePublishing.LABEL_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.LABEL_INPUT_LABEL,
        false,
        false,
        variantLabel

)
if((api.global.variantChanged && variantLabel)|| (!entry.getFirstInput().getValue() && variantLabel)) {
    entry.getFirstInput().setValue(variantLabel)
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue("")
}

return entry