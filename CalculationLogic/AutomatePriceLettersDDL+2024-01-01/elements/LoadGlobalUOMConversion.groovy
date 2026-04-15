if (api.isInputGenerationExecution()) return

api.global.globalUOMConversion = libs.QuoteLibrary.Conversion.getGlobalUOMConversion()

return null