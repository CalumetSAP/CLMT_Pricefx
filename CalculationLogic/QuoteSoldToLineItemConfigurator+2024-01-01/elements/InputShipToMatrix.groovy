import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def columnIds = [
        soldToConstants.MATRIX_SHIP_TO_ID,
        soldToConstants.MATRIX_REMOVE_ID,
        soldToConstants.MATRIX_BLOCKED_FLAG_ID,
        soldToConstants.MATRIX_CUSTOMER_MATERIAL_NUMBER_ID,
        soldToConstants.MATRIX_THIRD_PARTY_CUSTOMER_ID,
        soldToConstants.MATRIX_FREIGHT_TERM_ID,
        soldToConstants.MATRIX_INCOTERM_ID,
        soldToConstants.MATRIX_FREIGHT_REQUEST_ID,
        soldToConstants.MATRIX_NAMED_PLACE_ID,
        soldToConstants.MATRIX_FREIGHT_UOM_ID,
        soldToConstants.MATRIX_FREIGHT_AMOUNT_ID,
        soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID,
        soldToConstants.MATRIX_FREIGHT_VALID_TO_ID,
        soldToConstants.MATRIX_DELIVERED_PRICE_ID,
        soldToConstants.MATRIX_REJECTION_REASON_ID,
        soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID,
        soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID,
        soldToConstants.MATRIX_SHIP_TO_CITY_ID,
        soldToConstants.MATRIX_SHIP_TO_STATE_ID,
        soldToConstants.MATRIX_SHIP_TO_ZIP_ID,
        soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID,
        soldToConstants.MATRIX_SAP_CONTRACT_ID,
        soldToConstants.MATRIX_LINE_NUMBER_ID,
]
def columnLabels = [
        soldToConstants.MATRIX_SHIP_TO_LABEL,
        soldToConstants.MATRIX_REMOVE_LABEL,
        soldToConstants.MATRIX_BLOCKED_FLAG_LABEL,
        soldToConstants.MATRIX_CUSTOMER_MATERIAL_NUMBER_LABEL,
        soldToConstants.MATRIX_THIRD_PARTY_CUSTOMER_LABEL,
        soldToConstants.MATRIX_FREIGHT_TERM_LABEL,
        soldToConstants.MATRIX_INCOTERM_LABEL,
        soldToConstants.MATRIX_FREIGHT_REQUEST_LABEL,
        soldToConstants.MATRIX_NAMED_PLACE_LABEL,
        soldToConstants.MATRIX_FREIGHT_UOM_LABEL,
        soldToConstants.MATRIX_FREIGHT_AMOUNT_LABEL,
        soldToConstants.MATRIX_FREIGHT_VALID_FROM_LABEL,
        soldToConstants.MATRIX_FREIGHT_VALID_TO_LABEL,
        soldToConstants.MATRIX_DELIVERED_PRICE_LABEL,
        soldToConstants.MATRIX_REJECTION_REASON_LABEL,
        soldToConstants.MATRIX_SHIP_TO_INDUSTRY_LABEL,
        soldToConstants.MATRIX_SHIP_TO_ADDRESS_LABEL,
        soldToConstants.MATRIX_SHIP_TO_CITY_LABEL,
        soldToConstants.MATRIX_SHIP_TO_STATE_LABEL,
        soldToConstants.MATRIX_SHIP_TO_ZIP_LABEL,
        soldToConstants.MATRIX_SHIP_TO_COUNTRY_LABEL,
        soldToConstants.MATRIX_SAP_CONTRACT_LABEL,
        soldToConstants.MATRIX_LINE_NUMBER_LABEL,
]
def readOnlyColumns = [
        soldToConstants.MATRIX_BLOCKED_FLAG_ID,
        soldToConstants.MATRIX_DELIVERED_PRICE_ID,
        soldToConstants.MATRIX_SHIP_TO_INDUSTRY_ID,
        soldToConstants.MATRIX_SHIP_TO_ADDRESS_ID,
        soldToConstants.MATRIX_SHIP_TO_CITY_ID,
        soldToConstants.MATRIX_SHIP_TO_STATE_ID,
        soldToConstants.MATRIX_SHIP_TO_ZIP_ID,
        soldToConstants.MATRIX_SHIP_TO_COUNTRY_ID,
        soldToConstants.MATRIX_SAP_CONTRACT_ID,
        soldToConstants.MATRIX_LINE_NUMBER_ID,
]

def freightTermMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["FreightTerm"] as Map : [:]
def incotermMap = api.local.incotermOptions && !api.isInputGenerationExecution() ? api.local.incotermOptions as Map : [:]
def rejectionReasonMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["RejectionReason"] as Map : [:]

def shipToOptions = api.local.shipToOptions ?: []
def freightTermOptions = freightTermMap?.values()?.toList() ?: []
def incotermOptions = incotermMap?.values()?.toList() ?: []
def freightUOMOptions = api.local.freightUOMOptions && !api.isInputGenerationExecution() ? api.local.freightUOMOptions : []
def rejectionReasonOptions = rejectionReasonMap?.values()?.toList() ?: []

def columnValueOptions = [
        (soldToConstants.MATRIX_SHIP_TO_ID)         : shipToOptions,
        (soldToConstants.MATRIX_REMOVE_ID)          : ["Yes"],
        (soldToConstants.MATRIX_BLOCKED_FLAG_ID)    : ["Yes"],
        (soldToConstants.MATRIX_FREIGHT_TERM_ID)    : freightTermOptions,
        (soldToConstants.MATRIX_INCOTERM_ID)        : incotermOptions,
        (soldToConstants.MATRIX_FREIGHT_REQUEST_ID) : ["Yes"],
        (soldToConstants.MATRIX_FREIGHT_UOM_ID)     : freightUOMOptions,
        (soldToConstants.MATRIX_REJECTION_REASON_ID): rejectionReasonOptions,
]
def columnTypes = [
        InputType.OPTION,
        InputType.OPTION,
        InputType.OPTION,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.OPTION,
        InputType.OPTION,
        InputType.OPTION,
        InputType.STRINGUSERENTRY,
        InputType.OPTION,
        "Numeric",
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        "Numeric",
        InputType.OPTION,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
        InputType.STRINGUSERENTRY,
]
def hiddenColumns = [soldToConstants.MATRIX_LINE_ID_HIDDEN_ID]
def requiredColumns = [
        soldToConstants.MATRIX_FREIGHT_TERM_ID,
        soldToConstants.MATRIX_INCOTERM_ID,
        soldToConstants.MATRIX_FREIGHT_UOM_ID,
        soldToConstants.MATRIX_FREIGHT_AMOUNT_ID,
        soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID,
        soldToConstants.MATRIX_FREIGHT_VALID_TO_ID,
]
def defaults = []

def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputMatrix(
            lineItemConstants.SHIP_TO_MATRIX_ID,
            lineItemConstants.SHIP_TO_MATRIX_LABEL,
            columnIds,
            columnLabels,
            readOnlyColumns,
            columnValueOptions,
            columnTypes,
            true,
            false,
            false,
            false,
            true,
            defaults,
    ).getFirstInput()
    input.setConfigParameter("hiddenColumns", hiddenColumns)
    input.setConfigParameter("requiredColumns", hiddenColumns)
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SHIP_TO_MATRIX_ID,
            InputType.HIDDEN,
            lineItemConstants.SHIP_TO_MATRIX_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry