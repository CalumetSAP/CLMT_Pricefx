def customers = out.FindCustomers
def regions = out.FindShipTo
def cities = api.local.selectedShipToCity

if(cities) {
    regions = regions?.findAll{customers?.get(it.attribute1)?.attribute5 in cities}
}

def selectedShipTo = input?.get(libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY)

if(selectedShipTo) {
    regions = regions?.findAll{ it.attribute1 in selectedShipTo }
}

def variantShipToRegion = api.global.selectedVariant?.ShipToRegion

regions = regions?.collect{customers?.get(it.attribute1)?.attribute7}

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_REGION_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_REGION_INPUT_LABEL,
        false,
        false,
        null,
        regions
)

if((api.global.variantChanged && variantShipToRegion) || (!entry.getFirstInput().getValue() && variantShipToRegion)) {
    entry.getFirstInput().setValue(variantShipToRegion?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry