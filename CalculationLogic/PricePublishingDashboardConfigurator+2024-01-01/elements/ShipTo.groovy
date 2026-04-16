def customers = out.FindCustomers

def shipTos = out.FindShipTo

def soldTo = out.SoldTo.getFirstInput().getValue()
def regions = out.ShipToRegion.getFirstInput().getValue()
def cities = out.ShipToCity.getFirstInput().getValue()

if(soldTo) shipTos = shipTos.findAll{customers?.get(it.attribute1)?.customerId == it.attribute1}
if(regions) shipTos = shipTos.findAll{customers?.get(it.attribute1)?.attribute7 in regions}
if(cities) shipTos = shipTos.findAll{customers?.get(it.attribute1)?.attribute5 in cities}

shipTos = shipTos.collectEntries{[(it.attribute1): it.attribute1 + " - " + customers?.get(it.attribute1)?.name]}
def variantShipTos = api.global.selectedVariant?.ShipTo

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.SHIP_TO_INPUT_LABEL,
        false,
        false,
        null,
        shipTos
)

if((api.global.variantChanged && variantShipTos) || (!entry.getFirstInput().getValue() && variantShipTos)) {
    entry.getFirstInput().setValue(variantShipTos.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry