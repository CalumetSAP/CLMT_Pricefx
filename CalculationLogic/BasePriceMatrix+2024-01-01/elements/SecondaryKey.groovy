def secKey = []

if(api.global.pricing[api.local.sku]){
    def pricelists = api.global.pricing[api.local.sku]?.flatten() ?: []

    pricelists?.each{ pl ->
        def scales = api.global.ZDGS?.get(api.local.sku + "|" + pl)
        if(scales){
            scales?.each{ condRecNo, values ->
                values?.each{
                    secKey.add(pl + '-' + it.ScaleQuantity)
                }
            }
        }else{
            secKey.add(pl)
        }
    }
}

if(api.local.products?.contains(api.local.sku) && api.local.pricelist){
    def inputPricelists = api.local.pricelist?.flatten() ?: []

    inputPricelists?.each{ pl ->
        def scales = api.global.ZDGS?.get(api.local.sku + "|" + pl)
        if(scales){
            scales?.each{ condRecNo, values ->
                values?.each{
                    secKey.add(pl + '-' + it.ScaleQuantity)
                }
            }
        }else{
            secKey.add(pl)
        }
    }
}

return secKey?.unique()