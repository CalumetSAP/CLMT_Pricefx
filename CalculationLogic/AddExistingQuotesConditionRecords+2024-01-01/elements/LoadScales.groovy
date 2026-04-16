final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

def scalesMap = [:]
def scalesAux = []
def scales
api.local.quoteItems?.each {
    scalesAux = []
    scales = getInputByName(it?.inputs, lineItemInputsConstants.SCALES_CONFIGURATOR_NAME)?.get(lineItemInputsConstants.SCALES_ID)
    scales?.each {
        scalesAux.add(it.ScaleQty+"="+it.Price)
    }
    scalesMap.put(it.lineId, [
            Scale   : scalesAux.join("|"),
            ScaleUOM: scales?.find()?.ScaleUOM,
    ])
}

api.local.scalesMap = scalesMap

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}