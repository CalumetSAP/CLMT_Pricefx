if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

return libs.QuoteLibrary.Conversion.getGlobalUOMConversion()