if (libs.SharedLib.BatchUtils.isNewBatch()) {
    api.global.uomConversion = libs.QuoteLibrary.Conversion.getUOMConversion(api.global.batchKeys1?.toSet()?.toList(), api.global.products)
}

return null