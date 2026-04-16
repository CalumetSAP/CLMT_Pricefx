//if (out.InputPriceType?.getFirstInput()?.getValue() != "2") return
//if (out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() == "4") return
//
//final lineItem = libs.QuoteConstantsLibrary.LineItem
//
//def required = out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() == "3"
//def readOnly = false//api.local.exclusionData ? true : false
//
//def entry
//if (api.local.PPDefaultValues?.MovementDay || api.local.exclusionData?.MovementDay) {
//    entry = libs.BdpLib.UserInputs.createInputNumber(
//            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_ID,
//            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_LABEL,
//            required,
//            readOnly,
//            api.local.exclusionData?.MovementDay ? api.local.exclusionData?.MovementDay as Integer : api.local.PPDefaultValues?.MovementDay as Integer
//    )
//} else {
//    entry = libs.BdpLib.UserInputs.createInputNumber(
//            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_ID,
//            lineItem.PP_CONFIGURATOR_MOVEMENT_DAY_LABEL,
//            required,
//            readOnly
//    )
//}
//
//return entry