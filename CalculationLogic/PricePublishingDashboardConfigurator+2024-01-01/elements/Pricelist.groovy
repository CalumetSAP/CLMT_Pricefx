def selectedContracts = out.Contract.getFirstInput()?.getValue()
def pricelist = api.local.quotes

if(selectedContracts) {
    pricelist = pricelist?.findAll{it.SAPContractNumber in selectedContracts}
}

pricelist = pricelist?.collect{it.PriceListPLT}?.findAll{it} as List

def variantContracts = api.global.selectedVariant?.Pricelist

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.PRICELIST_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PRICELIST_INPUT_LABEL,
        false,
        false,
        null,
        pricelist as List
)
if((api.global.variantChanged && variantContracts) || (!entry.getFirstInput().getValue() && variantContracts)) {
    entry.getFirstInput().setValue(variantContracts?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry