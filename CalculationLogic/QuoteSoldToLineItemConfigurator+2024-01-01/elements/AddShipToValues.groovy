import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

def inputFormatter = new SimpleDateFormat("yyyy-MM-dd")
def sdf = new SimpleDateFormat("MM/dd/yyyy")

final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def inputValues = InputShipToMatrix.input.getValue() ?: []
def shipToLines = api.local.shipToLines ?: []
def invalidShipToList = api.local.invalidShipToList ?: []
def shipToData = out.FindShipToData ?: [:]
def freightTermMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["FreightTerm"] as Map : [:]
def incotermMap = api.local.incotermOptions && !api.isInputGenerationExecution() ? api.local.incotermOptions as Map : [:]
def rejectionReasonMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["RejectionReason"] as Map : [:]

def shipToItem, freightValidFrom, freightValidTo, lastShipTo, shipToBlocked
shipToLines?.each { line ->
    if (!inputValues.any { it.get(soldToConstants.MATRIX_LINE_ID_HIDDEN_ID) == line.LineID }) {
        shipToItem = shipToData?.get(line.ShipTo)
        freightValidFrom = line.FreightValidFrom ? inputFormatter.parse(line.FreightValidFrom) : null
        freightValidTo = line.FreightValidto ? inputFormatter.parse(line.FreightValidto) : null
        shipToBlocked = invalidShipToList?.contains(line.ShipTo) ? "Yes" : null
        inputValues.add([
                (soldToConstants.MATRIX_SHIP_TO_ID)                      : shipToItem?.ShipToName,
                (soldToConstants.MATRIX_REMOVE_ID)                       : null,
                (soldToConstants.MATRIX_BLOCKED_FLAG_ID)                 : shipToBlocked,
                (soldToConstants.MATRIX_CUSTOMER_MATERIAL_NUMBER_ID)     : line.CustomerMaterial,
                (soldToConstants.MATRIX_THIRD_PARTY_CUSTOMER_ID)         : line.ThirdPartyCustomer,
                (soldToConstants.MATRIX_FREIGHT_TERM_ID)                 : freightTermMap?.getOrDefault(line.FreightTerm, line.FreightTerm),
                (soldToConstants.MATRIX_INCOTERM_ID)                     : incotermMap?.getOrDefault(line.Incoterm, line.Incoterm),
                (soldToConstants.MATRIX_FREIGHT_REQUEST_ID)              : line.FreightEstimate ? "Yes" : null,
                (soldToConstants.MATRIX_NAMED_PLACE_ID)                  : line.NamedPlace,
                (soldToConstants.MATRIX_FREIGHT_UOM_ID)                  : line.FreightUOM,
                (soldToConstants.MATRIX_FREIGHT_AMOUNT_ID)               : line.FreightAmount,
                (soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID)           : freightValidFrom ? sdf.format(freightValidFrom) : null,
                (soldToConstants.MATRIX_FREIGHT_VALID_TO_ID)             : freightValidTo ? sdf.format(freightValidTo) : null,
                (soldToConstants.MATRIX_DELIVERED_PRICE_ID)              : line.DeliveredPrice,
                (soldToConstants.MATRIX_REJECTION_REASON_ID)             : rejectionReasonMap?.getOrDefault(line.RejectionReason, line.RejectionReason),
                (soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID)             : shipToItem?.Industry,
                (soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID)              : shipToItem?.Address,
                (soldToConstants.MATRIX_SHIP_TO_CITY_ID)                 : shipToItem?.City,
                (soldToConstants.MATRIX_SHIP_TO_STATE_ID)                : shipToItem?.State,
                (soldToConstants.MATRIX_SHIP_TO_ZIP_ID)                  : shipToItem?.Zip,
                (soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID)              : shipToItem?.Country,
                (soldToConstants.MATRIX_SAP_CONTRACT_ID)                 : line.SAPContract,
                (soldToConstants.MATRIX_LINE_NUMBER_ID)                  : line.LineNumber,
                (soldToConstants.MATRIX_LINE_ID_HIDDEN_ID)               : line.LineID,
                (soldToConstants.MATRIX_FREIGHT_HAS_CHANGED_ID_HIDDEN_ID): false,
        ])
        lastShipTo = shipToItem?.ShipToName
    }

}

if (out.InputNewRow.getFirstInput().getValue()) {
    def removedItem = inputValues.removeLast()
    inputValues.add([
            (soldToConstants.MATRIX_SHIP_TO_ID)                 : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_ID),
            (soldToConstants.MATRIX_REMOVE_ID)                  : removedItem?.get(soldToConstants.MATRIX_REMOVE_ID),
            (soldToConstants.MATRIX_BLOCKED_FLAG_ID)            : removedItem?.get(soldToConstants.MATRIX_BLOCKED_FLAG_ID),
            (soldToConstants.MATRIX_CUSTOMER_MATERIAL_NUMBER_ID): removedItem?.get(soldToConstants.MATRIX_CUSTOMER_MATERIAL_NUMBER_ID),
            (soldToConstants.MATRIX_THIRD_PARTY_CUSTOMER_ID)    : removedItem?.get(soldToConstants.MATRIX_THIRD_PARTY_CUSTOMER_ID),
            (soldToConstants.MATRIX_FREIGHT_TERM_ID)            : removedItem?.get(soldToConstants.MATRIX_FREIGHT_TERM_ID),
            (soldToConstants.MATRIX_INCOTERM_ID)                : removedItem?.get(soldToConstants.MATRIX_INCOTERM_ID),
            (soldToConstants.MATRIX_FREIGHT_REQUEST_ID)         : removedItem?.get(soldToConstants.MATRIX_FREIGHT_REQUEST_ID),
            (soldToConstants.MATRIX_NAMED_PLACE_ID)             : removedItem?.get(soldToConstants.MATRIX_NAMED_PLACE_ID),
            (soldToConstants.MATRIX_FREIGHT_UOM_ID)             : removedItem?.get(soldToConstants.MATRIX_FREIGHT_UOM_ID),
            (soldToConstants.MATRIX_FREIGHT_AMOUNT_ID)          : removedItem?.get(soldToConstants.MATRIX_FREIGHT_AMOUNT_ID),
            (soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID)      : removedItem?.get(soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID),
            (soldToConstants.MATRIX_FREIGHT_VALID_TO_ID)        : removedItem?.get(soldToConstants.MATRIX_FREIGHT_VALID_TO_ID),
            (soldToConstants.MATRIX_DELIVERED_PRICE_ID)         : removedItem?.get(soldToConstants.MATRIX_DELIVERED_PRICE_ID),
            (soldToConstants.MATRIX_REJECTION_REASON_ID)        : removedItem?.get(soldToConstants.MATRIX_REJECTION_REASON_ID),
            (soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID)        : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID),
            (soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID)         : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID),
            (soldToConstants.MATRIX_SHIP_TO_CITY_ID)            : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_CITY_ID),
            (soldToConstants.MATRIX_SHIP_TO_STATE_ID)           : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_STATE_ID),
            (soldToConstants.MATRIX_SHIP_TO_ZIP_ID)             : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_ZIP_ID),
            (soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID)         : removedItem?.get(soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID),
            (soldToConstants.MATRIX_SAP_CONTRACT_ID)            : removedItem?.get(soldToConstants.MATRIX_SAP_CONTRACT_ID),
            (soldToConstants.MATRIX_LINE_NUMBER_ID)             : removedItem?.get(soldToConstants.MATRIX_LINE_NUMBER_ID),
            (soldToConstants.MATRIX_LINE_ID_HIDDEN_ID)          : "NewLine",
    ])
    out.InputNewRow.getFirstInput().setValue(false)
}

if (isThereAnyEmptyRow(inputValues)) {
    out.InputNewRow.getFirstInput().setValue(true)
}

InputShipToMatrix.input.setValue(inputValues)

return null

def isThereAnyEmptyRow(List list) {
    return list.any { it.HiddenLineID == null}
}