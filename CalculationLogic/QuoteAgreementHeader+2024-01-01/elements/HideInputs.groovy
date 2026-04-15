//import net.pricefx.common.api.InputType
//
//if (!quoteProcessor.isPostPhase()) return
//
//def inputs
//if (api.isUserInGroup("Pricing", api.local.loginName) || api.isUserInGroup("Freight", api.local.loginName)) {
//    inputs = api.local.pricingAndFreightHiddenInputs ?: []
//} else if (api.isUserInGroup("Sales", api.local.loginName)) {
//    inputs = api.local.salesHiddenInputs ?: []
//}
//
//for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
//    if (lnProduct.folder) continue
//    String lineId = lnProduct.lineId as String
//
//    inputs?.each {
//        quoteProcessor.addOrUpdateInput(
//                lineId, [
//                "name": it,
//                "type": InputType.HIDDEN,
//        ])
//    }
//}