def selectedContracts = out.Contract.getFirstInput()?.getValue()
def plantNames = out.FindPlants
def plant = api.local.quotes

if(selectedContracts) {
    plant = plant?.findAll{it.SAPContractNumber in selectedContracts}
}

plant = plant?.collect{it.Plant}?.findAll{it}?.collectEntries { [(it): plantNames?.getOrDefault(it, it)] }

def variantContracts = api.global.selectedVariant?.Plant

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.PLANT_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PLANT_INPUT_LABEL,
        false,
        false,
        null,
        plant as Map
)
if((api.global.variantChanged && variantContracts) || (!entry.getFirstInput().getValue() && variantContracts)) {
    entry.getFirstInput().setValue(variantContracts?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry