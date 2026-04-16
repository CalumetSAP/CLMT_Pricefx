def selectedContracts = out.Contract.getFirstInput()?.getValue()
def salesPersonMap = out.FindSalesPerson
def salesPerson = api.local.quotes

if(selectedContracts) {
    salesPerson = salesPerson?.findAll{it.SAPContractNumber in selectedContracts}
}

salesPerson = salesPerson?.collect{it.SalesPerson}?.findAll{it}?.collectEntries { [(it): salesPersonMap?.getOrDefault(it, it)] }

def variantContracts = api.global.selectedVariant?.SalesPerson

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.SALES_PERSON_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SALES_PERSON_INPUT_LABEL,
        false,
        false,
        null,
        salesPerson as Map
)
if((api.global.variantChanged && variantContracts) || (!entry.getFirstInput().getValue() && variantContracts)) {
    entry.getFirstInput().setValue(variantContracts?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry