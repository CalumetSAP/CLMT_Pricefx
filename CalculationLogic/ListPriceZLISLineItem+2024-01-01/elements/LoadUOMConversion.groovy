if (libs.SharedLib.BatchUtils.isNewBatch()) {
    api.global.uomConversion = libs.QuoteLibrary.Conversion.getUOMConversion(api.global.currentBatch, api.global.products)
}

return api.global.uomConversion