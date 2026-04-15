import net.pricefx.common.api.InputType

def fields = ['name', 'attribute1']
api.local.pricelists = api.findLookupTableValues("Pricelist", fields, null).collect{ it }.unique()

def ce = api.createConfiguratorEntry(InputType.OPTIONS,"PriclistsInput")

ce.getFirstInput().setLabel("Pricelist(s)")
//ce.getFirstInput().setRequired(true)

Map pricelists = [:]
api.local.pricelists.each { pricelist ->
    pricelists[pricelist.name] = String.join(" - ", pricelist.name, pricelist.attribute1)
}
def pricelistsNames = pricelists?.keySet()?.toList()

if(pricelistsNames?.size() > 0){
    ce.getFirstInput().setValueOptions(pricelistsNames)
    ce.getFirstInput().setConfigParameter("labels", pricelists)
}

return ce