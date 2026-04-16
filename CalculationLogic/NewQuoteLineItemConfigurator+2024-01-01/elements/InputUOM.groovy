//import net.pricefx.common.api.InputType
//
//def havePermissions = api.local.isPricingGroup
//
//final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
//def entry = null
//if (havePermissions) {
//    input = libs.BdpLib.UserInputs.createInputString(
//            lineItemConstants.UOM_ID,
//            lineItemConstants.UOM_LABEL,
//            false,
//            true,
//            api.local.product?.UOM as String
//    ).getFirstInput()
//} else {
//    entry = libs.BdpLib.UserInputs.createInput(
//            lineItemConstants.UOM_ID,
//            InputType.HIDDEN,
//            lineItemConstants.UOM_LABEL,
//            false,
//            true,
//    )
//    input = entry.getFirstInput()
//}
//
//return entry