def newListPriceOverride = api.getManualOverride("NewListPrice")
def newListPriceModifiedDatetimeOverride = api.currentContext(api.local.material, api.local.secondaryKey)?.get("NewListPriceModifiedDatetime")
def newListPrice = out.NewListPriceValue

//row edition
if(api.currentItem("pricelistId") && api.global.currentBatch?.size() == 1 && newListPriceOverride != newListPrice){
    return new Date()
}

return newListPriceModifiedDatetimeOverride