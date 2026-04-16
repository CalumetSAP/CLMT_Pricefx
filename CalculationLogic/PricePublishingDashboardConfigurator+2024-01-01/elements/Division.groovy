def divisions = out.FindDivision.collect{divisions -> divisions.name}?.findAll{it == "20" || it == "30"}

String variantDivision = api.global.selectedVariant?.Division

def entry = libs.BdpLib.UserInputs.createInputOption(
        libs.DashboardConstantsLibrary.PricePublishing.DIVISION_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.DIVISION_INPUT_LABEL,
        false,
        false,
        divisions,
        null
)

if((api.global.variantChanged && variantDivision) || (!entry.getFirstInput().getValue() && variantDivision)) {
    entry.getFirstInput().setValue(variantDivision)
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry