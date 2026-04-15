import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution()) return

def inputs
if (api.isUserInGroup("Pricing", api.local.loginName) || api.isUserInGroup("Freight", api.local.loginName)) {
    inputs = api.local.pricingAndFreightInputs ?: [:]
} else if (api.isUserInGroup("Sales", api.local.loginName)) {
    inputs = api.local.salesInputs ?: [:]
}

// Hide all inputs
def allInputs = api.local.allInputs
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    allInputs?.each {
        quoteProcessor.addOrUpdateInput(
                lineId, [
                "name": it,
                "type": InputType.HIDDEN,
        ])
    }
}

// Hide all outputs
//def allOutputs = api.local.allOutputs
//for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
//    if (lnProduct.folder) continue
//    String lineId = lnProduct.lineId as String
//
//    allOutputs?.each {
//        quoteProcessor.addOrUpdateOutput(
//                lineId, [
//                "resultName": it,
//                "formatType": FieldFormatType.
//        ])
//    }
//}

// Show inputs by user group
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String
    def map = [:]
    inputs?.each { key, value ->
        map = [:]
        map.put("name", key)
        map.put("type", value.Type)
        if (value.DataType) map.put("dataType", value.DataType)
        if (value.URL) {
            map.put("formulaName", value.URL)
            map.put("width", value.Width)
            map.put("height", value.Height)
        }
        quoteProcessor.addOrUpdateInput(lineId, map)
    }
}