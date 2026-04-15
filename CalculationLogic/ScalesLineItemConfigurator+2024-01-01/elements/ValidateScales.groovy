if (api.isInputGenerationExecution()) return

final roundingUtils = libs.QuoteLibrary.RoundingUtils

//def selectedScaleQty = InputScales.input?.getValue()?.ScaleQty
//def selectedPrice = InputScales.input?.getValue()?.Price

//if (!selectedScaleQty && !selectedPrice) return
def scaleUOM, priceUOM
InputScales.input?.getValue()?.eachWithIndex { value, i ->
    if (value.ScaleQty) {
        if (value.ScaleQty.toString().isNumber()) {
            value.ScaleQty = value.ScaleQty.toString().toBigDecimal().toInteger()
        } else {
            value.ScaleQty = null
        }
    }
    if (value.Price) {
        if (value.Price.toString().isNumber()) {
            value.Price = roundingUtils.round(value.Price.toString().toBigDecimal(), api.local.numberOfDecimals)
        } else {
            value.Price = null
        }
    }

    if (api.local.MOQUOM && !value.ScaleUOM) value.ScaleUOM = api.local.MOQUOM
    if (api.local.priceUOM) value.PriceUOM = api.local.priceUOM

    if (i == 0) {
        scaleUOM = value.ScaleUOM
        priceUOM = value.PriceUOM
    } else {
        value.ScaleUOM = scaleUOM
        value.PriceUOM = priceUOM
    }
}