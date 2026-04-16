def customers = out.FindCustomers
def regions = out.ShipToRegion?.getFirstInput()?.getValue()
def cities = out.FindShipTo

if(regions) {
    cities = cities?.findAll{customers?.get(it.attribute1)?.attribute7 in regions}
}

def selectedShipTo = input?.get(libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY)

if(selectedShipTo) {
    cities = cities?.findAll{ it.attribute1 in selectedShipTo }
}

cities = cities?.collect { customers?.get(it.attribute1)?.attribute5 }

def variantShipToCity = api.global.selectedVariant?.ShipToCity

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_CITY_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_CITY_INPUT_LABEL,
        false,
        false,
        null,
        cities
)

if((api.global.variantChanged && variantShipToCity) || (!entry.getFirstInput().getValue() && variantShipToCity)) {
    entry.getFirstInput().setValue(variantShipToCity?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

if(entry.getFirstInput()?.getValue()) api.local.selectedShipToCity = entry.getFirstInput()?.getValue()

return entry