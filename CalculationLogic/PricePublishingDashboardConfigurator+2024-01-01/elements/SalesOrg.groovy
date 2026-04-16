def salesOrgs = out.FindSalesOrgs
def variantSalesOrg = api.global.selectedVariant?.SalesOrg
String selectedVariantName = out.Variant?.getFirstInput()?.getValue()

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.SALES_ORG_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SALES_ORG_INPUT_LABEL,
        false,
        false,
        null,
        salesOrgs,
)

if((api.global.variantChanged && variantSalesOrg) || (!entry.getFirstInput().getValue() && variantSalesOrg)) {
    entry.getFirstInput().setValue(variantSalesOrg?.unique())
}

if(selectedVariantName == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue([])
}

return entry