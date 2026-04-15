String selectedVariantName = out.Variant?.getFirstInput()?.getValue()
api.local.selectedVariant = out.FindVariants.find{it.key1 == selectedVariantName}

def entry = libs.BdpLib.UserInputs.createInputString(
        libs.DashboardConstantsLibrary.PricePublishing.VARIANT_NAME_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.VARIANT_NAME_INPUT_LABEL,
        false,
        false,
        selectedVariantName,
)

entry.setMessage("<span style='color:red;font-weight:bold;'>*Apply Settings before saving New Variant</span>")

if((api.global.variantChanged && selectedVariantName) || (!entry.getFirstInput().getValue() && selectedVariantName)) {
    entry.getFirstInput().setValue(selectedVariantName)
}

if(selectedVariantName == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue("")
}

return entry