import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def globalUOMConversionMap = api.local.globalUOMTable ?: [:]
def uomConversionMap = api.local.uomTable ?: [:]
def shipToData = out.FindShipToData ?: [:]
def invalidShipToList = api.local.invalidShipToList ?: []
def freightTermMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["FreightTerm"] as Map : [:]
def shipToLines = api.local.shipToLines ?: []
def existingContracts = out.FindExistingContracts ?: [:]

def inputValues = InputShipToMatrix?.input?.getValue() ?: []
def sku = InputMaterial?.input?.getValue()
def price = InputPrice?.entry?.getFirstInput()?.getValue()?.toBigDecimal()
def pricingUOM = InputPricingUOM?.input?.getValue()

def visitedLines = []
def freightUOM, freightTerm, conversionFactor, convertedFreight, deliveredPrice, shipTo, shipToItem, shipToBlocked,
        existingLine, existingLineShipTo, freightHasChanged, wasVisited, sapContract, sapLineID, hiddenLineID
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

    wasVisited = visitedLines.contains(it[soldToConstants.MATRIX_LINE_ID_HIDDEN_ID])

    existingLine = wasVisited ? null : shipToLines?.find { line -> line.LineID == it[soldToConstants.MATRIX_LINE_ID_HIDDEN_ID] }
    existingLineShipTo = existingLine ? shipToData?.get(existingLine.ShipTo)?.ShipToName : it[soldToConstants.MATRIX_SHIP_TO_ID]

    sapContract = wasVisited ? null : it[soldToConstants.MATRIX_SAP_CONTRACT_ID]
    sapLineID = wasVisited ? null : it[soldToConstants.MATRIX_LINE_NUMBER_ID]
    hiddenLineID = wasVisited ? "NewLine" : it[soldToConstants.MATRIX_LINE_ID_HIDDEN_ID]
    sapContract = hiddenLineID != "NewLine" ? sapContract : existingContracts?.getOrDefault(shipTo, null)
    freightHasChanged = trackFreightChanges(existingLine, it, freightTermMap)

    if (it[soldToConstants.MATRIX_LINE_ID_HIDDEN_ID] != "NewLine") visitedLines.add(it[soldToConstants.MATRIX_LINE_ID_HIDDEN_ID])

    return it + [
            (soldToConstants.MATRIX_SHIP_TO_ID)                      : existingLineShipTo,
            (soldToConstants.MATRIX_REMOVE_ID)                       : existingLine ? it[soldToConstants.MATRIX_REMOVE_ID] : null,
            (soldToConstants.MATRIX_REJECTION_REASON_ID)             : existingLine ? it[soldToConstants.MATRIX_REJECTION_REASON_ID] : null,
            (soldToConstants.MATRIX_BLOCKED_FLAG_ID)                 : shipToBlocked,
            (soldToConstants.MATRIX_DELIVERED_PRICE_ID)              : deliveredPrice,
            (soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID)             : shipToItem?.Industry,
            (soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID)              : shipToItem?.Address,
            (soldToConstants.MATRIX_SHIP_TO_CITY_ID)                 : shipToItem?.City,
            (soldToConstants.MATRIX_SHIP_TO_STATE_ID)                : shipToItem?.State,
            (soldToConstants.MATRIX_SHIP_TO_ZIP_ID)                  : shipToItem?.Zip,
            (soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID)              : shipToItem?.Country,
            (soldToConstants.MATRIX_SAP_CONTRACT_ID)                 : sapContract,
            (soldToConstants.MATRIX_LINE_NUMBER_ID)                  : sapLineID,
            (soldToConstants.MATRIX_LINE_ID_HIDDEN_ID)               : hiddenLineID,
            (soldToConstants.MATRIX_FREIGHT_HAS_CHANGED_ID_HIDDEN_ID): freightHasChanged,
    ]

}

InputShipToMatrix?.input?.setValue(newValue)

return null

def trackFreightChanges(existingLine, line, Map freightTermMap) {
    if (!existingLine) return true

    final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

    def inputFormatter = new SimpleDateFormat("yyyy-MM-dd")
    def sdf = new SimpleDateFormat("MM/dd/yyyy")

    def freightValidFrom = existingLine.FreightValidFrom ? inputFormatter.parse(existingLine.FreightValidFrom) : null
    def freightValidTo = existingLine.FreightValidto ? inputFormatter.parse(existingLine.FreightValidto) : null

    freightValidFrom = freightValidFrom ? sdf.format(freightValidFrom) : null
    freightValidTo = freightValidTo ? sdf.format(freightValidTo) : null

    def checks = [
            existingLine.FreightAmount != line[soldToConstants.MATRIX_FREIGHT_AMOUNT_ID],
            freightValidFrom != line[soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID],
            freightValidTo != line[soldToConstants.MATRIX_FREIGHT_VALID_TO_ID],
            existingLine.FreightUOM != line[soldToConstants.MATRIX_FREIGHT_UOM_ID],
    ]

    def previousFreightTerm = existingLine.FreightTerm
    def freightTerm = line[soldToConstants.MATRIX_FREIGHT_TERM_ID] as String
    freightTerm = freightTerm ? freightTermMap?.find { k, v -> v.toString().startsWith(freightTerm) }?.key : freightTerm

    return checks.any { it } || freightTermHasChanged(previousFreightTerm, freightTerm)
}

def freightTermHasChanged(previousFreight, currentFreight) {
    return ((previousFreight == "1" || previousFreight == "2") && (currentFreight == "3" || currentFreight == "4")) ||
            ((previousFreight == "3" || previousFreight == "4") && (currentFreight == "1" || currentFreight == "2"))
}