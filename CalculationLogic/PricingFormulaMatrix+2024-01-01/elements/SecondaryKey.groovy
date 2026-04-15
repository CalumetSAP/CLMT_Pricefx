def secondaryKeys = []

secondaryKeys.addAll(getSecondaryKeys(api.global.quotes[api.local.sku]))

return secondaryKeys?.unique()

def getSecondaryKeys(quotesRows) {
    return quotesRows?.collect { (it.SoldTo?:"")+"-"+(it.ShipTo?:"")+"-"+(it.SAPContractNumber?:"")+"-"+(it.SAPLineID?:"") } ?: []
}