if (api.isInputGenerationExecution()) return

final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

String quoteId = api.isDebugMode() ? "1085.Q" : dist?.calcItem?.Key2

loader = api.isDebugMode() ? [] : dist?.dataLoader

def quote = api.getCalculableLineItemCollection(quoteId)

if (quote?.get("quoteType") != "New Contract" && quote?.get("quoteType") != "NewContract") return

def quoteItems = quote?.lineItems
def row, lnConfigurator, scaleMatrixValues, scaleQty, scalePrice

for (quoteItem in quoteItems) {
    lnConfigurator = quoteItem?.inputs?.find { it.name == lineItemInputsConstants.SCALES_CONFIGURATOR_NAME }?.value
    scaleMatrixValues = lnConfigurator?.get(lineItemInputsConstants.SCALES_ID)
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
//api.trace("loader", loader)