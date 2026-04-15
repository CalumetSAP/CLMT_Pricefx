if (api.isInputGenerationExecution() || !api.local.lineItemSkus) return

return libs.QuoteLibrary.Conversion.getGlobalUOMConversion()