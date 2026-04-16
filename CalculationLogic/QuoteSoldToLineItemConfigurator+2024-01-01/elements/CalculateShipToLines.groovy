if (api.isInputGenerationExecution()) return

final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]
def shipToData = out.FindShipToData ?: [:]
def invalidShipToList = api.local.invalidShipToList ?: []
def freightTermMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["FreightTerm"] as Map : [:]
def shipToLines = api.local.shipToLines ?: []

def inputValues = InputShipToMatrix?.input?.getValue() ?: []
def sku = InputMaterial?.input?.getValue()
def price = InputPrice?.entry?.getFirstInput()?.getValue()?.toBigDecimal()
def pricingUOM = InputPricingUOM?.input?.getValue()

def freightUOM, freightTerm, conversionFactor, convertedFreight, deliveredPrice, shipTo, shipToItem, shipToBlocked, existingLine, existingLineShipTo
def newValue = inputValues.collect {
    deliveredPrice = null
    freightTerm = it[soldToConstants.MATRIX_FREIGHT_TERM_ID]
    freightUOM = it[soldToConstants.MATRIX_FREIGHT_UOM_ID]
    if (freightTerm == freightTermMap?.get("1") || freightTerm == freightTermMap?.get("2")) {
        deliveredPrice = price
    } else if (freightUOM) {
        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, freightUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)

        if (!conversionFactor) {
//            api.throwException("Missing conversion from Freight UOM (${freightUOM}) to Price UOM (${pricingUOM}) for material ${sku}")
        } else {
            convertedFreight = it[soldToConstants.MATRIX_FREIGHT_AMOUNT_ID] ? it[soldToConstants.MATRIX_FREIGHT_AMOUNT_ID] * conversionFactor : BigDecimal.ZERO
            deliveredPrice = price + convertedFreight
        }
    }

    shipTo = it[soldToConstants.MATRIX_SHIP_TO_ID]?.toString()?.split(" - ")?.getAt(0)
    shipToItem = shipTo ? shipToData?.get(shipTo) : [:]
    shipToBlocked = shipTo ? (invalidShipToList?.contains(shipTo) ? "Yes" : null ) : null

    existingLine = shipToLines?.find { line -> line.LineID == it[soldToConstants.MATRIX_LINE_ID_HIDDEN_ID] }
    existingLineShipTo = existingLine ? shipToData?.get(existingLine.ShipTo)?.ShipToName : it[soldToConstants.MATRIX_SHIP_TO_ID]

    return it + [
            (soldToConstants.MATRIX_SHIP_TO_ID)         : existingLineShipTo,
            (soldToConstants.MATRIX_REMOVE_ID)          : existingLine ? it[soldToConstants.MATRIX_REMOVE_ID] : null,
            (soldToConstants.MATRIX_REJECTION_REASON_ID): existingLine ? it[soldToConstants.MATRIX_REJECTION_REASON_ID] : null,
            (soldToConstants.MATRIX_BLOCKED_FLAG_ID)    : shipToBlocked,
            (soldToConstants.MATRIX_DELIVERED_PRICE_ID) : deliveredPrice,
            (soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID): shipToItem?.Industry,
            (soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID) : shipToItem?.Address,
            (soldToConstants.MATRIX_SHIP_TO_CITY_ID)    : shipToItem?.City,
            (soldToConstants.MATRIX_SHIP_TO_STATE_ID)   : shipToItem?.State,
            (soldToConstants.MATRIX_SHIP_TO_ZIP_ID)     : shipToItem?.Zip,
            (soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID) : shipToItem?.Country,
    ]

}

InputShipToMatrix?.input?.setValue(newValue)