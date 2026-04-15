//def variantDisplay = api.global.selectedVariant?.attribute12
//
//def entry = libs.BdpLib.UserInputs.createInputCheckbox(
//        libs.DashboardConstantsLibrary.PricePublishing.DISPLAY_INPUT_KEY,
//        libs.DashboardConstantsLibrary.PricePublishing.DISPLAY_INPUT_LABEL,
//        false,
//        false,
//        null
//)
//if((api.global.variantChanged && variantDisplay) || (!entry.getFirstInput().getValue() && variantDisplay)) {
//    entry.getFirstInput().setValue(variantDisplay)
//}
//
//return entry