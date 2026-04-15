if (api.isInputGenerationExecution()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def shipTo = api.global.outputsMap?.get(api.local.outputKey)?.get(lineItemConstants.SHIP_TO_ID) ?: out.HiddenInputs?.get("ShipTo")

if (api.global.invalidShipTos?.contains(shipTo)) {
    api.addWarning("Ship To is no longer valid")
}

return shipTo