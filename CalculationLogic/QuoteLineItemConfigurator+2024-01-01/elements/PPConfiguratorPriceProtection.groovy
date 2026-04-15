//if (out.InputPriceType?.getFirstInput()?.getValue() != "2") return
//
//final lineItem = libs.QuoteConstantsLibrary.LineItem
//
//def options = !api.isInputGenerationExecution() && api.local.priceProtectionOptions ? api.local.priceProtectionOptions : [:]
//def readOnly = false//api.local.exclusionData ? true : false
//
//def entry = libs.BdpLib.UserInputs.createInputOption(
//        lineItem.PP_CONFIGURATOR_PRICE_PROTECTION_ID,
//        lineItem.PP_CONFIGURATOR_PRICE_PROTECTION_LABEL,
//        false,
//        readOnly,
//        api.local.exclusionData?.PriceProtection ? api.local.exclusionData?.PriceProtection as Object : api.local.PPDefaultValues?.PriceProtection as Object,
//        options as Map
//)
//
//return entry