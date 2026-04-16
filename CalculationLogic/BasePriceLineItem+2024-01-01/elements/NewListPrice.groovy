import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

def newPrice = out.NewListPriceValue ?: api.currentContext(api.local.material, api.local.secondaryKey)?.get("NewListPrice")
def lastModifiedNewPrice = out.NewListPriceModifiedDatetime
if(lastModifiedNewPrice){
    lastModifiedNewPrice = sdf.parse(lastModifiedNewPrice)
}

def pricelists = api.global.PricingMap[api.local.material]?.keySet() ?: []
def inputPricelists = api.local.pricelists?.flatten() ?: []

(inputPricelists + pricelists)?.each{ pl ->
    def scales = api.global.ZDGS?.get(api.local.material + "|" + pl)
    if(scales){
        scales?.each{ condRecNo, values ->
            values?.each{
                if(api.local.secondaryKey != (pl + "-" + it.ScaleQuantity)){
                    def item = api.currentContext(api.local.material, pl + "-" + it.ScaleQuantity)

                    if(item?.get("NewListPriceModifiedDatetime") && item?.get("NewListPrice")){
                        def modified = sdf.parse(item?.get("NewListPriceModifiedDatetime"))
                        if(modified && (!lastModifiedNewPrice || lastModifiedNewPrice < modified)){
                            lastModifiedNewPrice = modified
                            newPrice = item?.get("NewListPrice")
                        }
                    }
                }
            }
        }
    }else{
        if(pl != api.local.pricelist){
            def item = api.currentContext(api.local.material, pl)

            if(item?.get("NewListPriceModifiedDatetime") && item?.get("NewListPrice")){
                def modified = sdf.parse(item?.get("NewListPriceModifiedDatetime"))
                if(modified && (!lastModifiedNewPrice || lastModifiedNewPrice < modified)){
                    lastModifiedNewPrice = modified
                    newPrice = item?.get("NewListPrice")
                }
            }
        }
    }
}

return api.attributedResult(newPrice).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())
