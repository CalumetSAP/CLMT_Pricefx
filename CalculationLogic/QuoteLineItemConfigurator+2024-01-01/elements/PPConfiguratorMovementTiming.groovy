//if (out.InputPriceType?.getFirstInput()?.getValue() != "2") return
//if (out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() == "4") return
//
//final lineItem = libs.QuoteConstantsLibrary.LineItem
//
//def options = !api.isInputGenerationExecution() && api.local.movementTimingOptions ? api.local.movementTimingOptions as Map : [:]
//def required = out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() == "3"
//def readOnly = false//api.local.exclusionData ? true : false
//
//def entry = libs.BdpLib.UserInputs.createInputOption(
//        lineItem.PP_CONFIGURATOR_MOVEMENT_TIMING_ID,
//        lineItem.PP_CONFIGURATOR_MOVEMENT_TIMING_LABEL,
//        required,
//        readOnly,
//        api.local.exclusionData?.MovementTiming ? api.local.exclusionData?.MovementTiming as Object : api.local.PPDefaultValues?.MovementTiming as Object,
//        options
//)
//
//return entry