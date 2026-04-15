//if (out.InputPriceType?.getFirstInput()?.getValue() != "2") return
//if (out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() == "4") return
//if (out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() != "1" &&
//        out.PPConfiguratorPriceProtection?.getFirstInput()?.getValue() != "2") return
//
//final lineItem = libs.QuoteConstantsLibrary.LineItem
//
//def readOnly = false//api.local.exclusionData ? true : false
//
//def entry
//if (api.local.PPDefaultValues?.NumberOfDays || api.local.exclusionData?.NumberOfDays) {
//    entry = libs.BdpLib.UserInputs.createInputNumber(
//            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID,
//            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_LABEL,
//            true,
//            readOnly,
//            api.local.exclusionData?.NumberOfDays ? api.local.exclusionData?.NumberOfDays as Integer : api.local.PPDefaultValues?.NumberOfDays as Integer
//    )
//} else {
//    entry = libs.BdpLib.UserInputs.createInputNumber(
//            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID,
//            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_LABEL,
//            true,
//            readOnly
//    )
//}
//
//return entry