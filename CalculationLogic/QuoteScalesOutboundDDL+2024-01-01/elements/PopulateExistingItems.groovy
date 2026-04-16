if (api.isInputGenerationExecution()) return

final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

String quoteId = api.isDebugMode() ? "1085.Q" : dist?.calcItem?.Key2

loader = api.isDebugMode() ? [] : dist?.dataLoader

def quote = api.getCalculableLineItemCollection(quoteId)

if (quote?.get("quoteType") != "ExistingContractUpdate") return

def quoteItems = quote?.lineItems
def row, lnConfigurator, scalesConfigurator, scaleMatrixValues, scaleQty, scalePrice

for (quoteItem in quoteItems) {
    lnConfigurator = getInputByName(quoteItem?.inputs, lineItemInputsConstants.NEW_QUOTE_CONFIGURATOR_NAME)
    if (lnConfigurator?.get(lineItemInputsConstants.PRICE_TYPE_ID) != "2") continue
    scalesConfigurator = getInputByName(quoteItem?.inputs, lineItemInputsConstants.SCALES_CONFIGURATOR_NAME)
    scaleMatrixValues = scalesConfigurator?.get(lineItemInputsConstants.SCALES_ID)
    for (scaleMatrixValue in scaleMatrixValues) {
        scaleQty = scaleMatrixValue.ScaleQty
        scalePrice = scaleMatrixValue.Price
        if (scaleQty != null && scalePrice != null) {
            row = [
                    "QuoteID"   : quote?.uniqueName,
                    "LineID"    : quoteItem?.lineId,
                    "ScaleID"   : scaleQty,
                    "ScaleQty"  : scaleQty,
                    "ScaleUOM"  : scaleMatrixValue.ScaleUOM,
                    "Price"     : scalePrice,
                    "PriceUOM"  : scaleMatrixValue.PriceUOM
            ]
            loader.addRow(row)
        }
    }
}

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}