//if (out.InputPriceType?.getFirstInput()?.getValue() != "2") return
//if (out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() == "4") return
//
//final lineItem = libs.QuoteConstantsLibrary.LineItem
//
//def required = out.PPConfiguratorMovementTiming?.getFirstInput()?.getValue() == "Quarter" && out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() != "4"
//def readOnly = false//api.local.exclusionData ? true : false
//def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"]
//
//def entry = libs.BdpLib.UserInputs.createInputOption(
//        lineItem.PP_CONFIGURATOR_MOVEMENT_START_ID,
//        lineItem.PP_CONFIGURATOR_MOVEMENT_START_LABEL,
//        required,
//        readOnly,
//        options,
//        api.local.exclusionData?.MovementStart?.toString() ? api.local.exclusionData?.MovementStart?.toString() as Object : api.local.PPDefaultValues?.MovementStart?.toString() as Object
//)
//
//return entry