def soldTo = out.FindSoldTo
def variantSoldTos = api.global.selectedVariant?.SoldTo
def customers = out.FindCustomers

soldTo = soldTo.collectEntries{ [(it.customerId): it.customerId + " - " + customers?.get(it.customerId)?.name] }

// prefilter by selected shipTo if it was selected.

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SOLD_TO_INPUT_LABEL,
        false,
        false,
        null,
        soldTo
)
if((api.global.variantChanged && variantSoldTos) || (!entry.getFirstInput().getValue() && variantSoldTos)) {
    entry.getFirstInput().setValue(variantSoldTos?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry