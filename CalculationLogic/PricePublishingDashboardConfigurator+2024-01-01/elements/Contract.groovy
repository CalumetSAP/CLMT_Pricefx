def contracts = api.local.quotes?.collect{it.SAPContractNumber}?.findAll{it}
def variantContracts = api.global.selectedVariant?.Contract

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_INPUT_LABEL,
        false,
        false,
        null,
        contracts as List
)
if((api.global.variantChanged && variantContracts) || (!entry.getFirstInput().getValue() && variantContracts)) {
    entry.getFirstInput().setValue(variantContracts?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry