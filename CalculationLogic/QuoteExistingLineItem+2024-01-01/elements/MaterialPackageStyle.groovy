if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

return api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.MATERIAL_PACKAGE_STYLE_ID) ?: out.HiddenInputs?.get("MaterialPackageStyle")