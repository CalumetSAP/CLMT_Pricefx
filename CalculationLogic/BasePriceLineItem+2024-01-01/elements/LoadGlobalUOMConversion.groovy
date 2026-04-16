if(api.global.isFirstRow){
    api.global.globalUOMConversion = libs.PricelistLib.Conversion.getGlobalUOMConversion()
}

return api.global.globalUOMConversion