def selectedContracts = out.Contract.getFirstInput()?.getValue()
def selectedPricelists = out.Pricelist.getFirstInput()?.getValue()
def contractLines = api.local.quotes

if(selectedContracts) {
    contractLines = contractLines?.findAll{it.SAPContractNumber in selectedContracts}
}

if(selectedPricelists){
    contractLines = contractLines?.findAll{it.SAPContractNumber in selectedPricelists}
}

contractLines = contractLines?.collect{it.SAPLineId} as List

def variantContractLines = api.global.selectedVariant?.ContractLine

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_LINE_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.CONTRACT_LINE_INPUT_LABEL,
        false,
        false,
        null,
        contractLines
)
if((api.global.variantChanged && variantContractLines) || (!entry.getFirstInput().getValue() && variantContractLines)) {
    entry.getFirstInput().setValue(variantContractLines?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry