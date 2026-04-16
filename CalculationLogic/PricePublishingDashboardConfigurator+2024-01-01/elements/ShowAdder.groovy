def variantShowAdder = api.global.selectedVariant?.ShowAdder

def entry = libs.BdpLib.UserInputs.createInputCheckbox(
        libs.DashboardConstantsLibrary.PricePublishing.SHOW_ADDER_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SHOW_ADDER_INPUT_LABEL,
        false,
        false,
        null
)

if((api.global.variantChanged && variantShowAdder) || (!entry.getFirstInput().getValue() && variantShowAdder)) {
    entry.getFirstInput().setValue(variantShowAdder)
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
    out.Variant?.getFirstInput()?.setValue()
}

if(api.global.variantChanged || out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL || api.global.selectedVariant){
//    out.Variant?.getFirstInput()?.setValue()
}

return entry