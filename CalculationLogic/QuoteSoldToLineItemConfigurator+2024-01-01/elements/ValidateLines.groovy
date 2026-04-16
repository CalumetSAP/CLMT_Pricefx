import java.text.SimpleDateFormat
import java.util.regex.Pattern

if (api.isInputGenerationExecution()) return

final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def inputValues = InputShipToMatrix?.input?.getValue() ?: []

validateExistingRow(inputValues)

inputValues?.each {
    validateDateFormat(it[soldToConstants.MATRIX_FREIGHT_VALID_FROM_ID], "Freight Valid From", it[soldToConstants.MATRIX_SHIP_TO_ID])
    validateDateFormat(it[soldToConstants.MATRIX_FREIGHT_VALID_TO_ID], "Freight Valid To", it[soldToConstants.MATRIX_SHIP_TO_ID])
}

return null

def validateExistingRow(values) {
    final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

    def seen = new HashSet()
    boolean validation = values.any { line ->
        def key = [
                line[soldToConstants.MATRIX_SHIP_TO_ID],
                line[soldToConstants.MATRIX_FREIGHT_TERM_ID],
                line[soldToConstants.MATRIX_INCOTERM_ID],
                line[soldToConstants.MATRIX_FREIGHT_UOM_ID],
                line[soldToConstants.MATRIX_NAMED_PLACE_ID],
        ]
        !seen.add(key)
    }

    if (validation) api.throwException("This combination already exists. Please update the existing row")

    return null
}

def validateDateFormat(value, fieldName, soldTo) {
    if (!value) return

    def dateFormat = new SimpleDateFormat("MM/dd/yyyy")
    dateFormat.setLenient(false)

    def datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")

    if (!datePattern.matcher(value).matches()) {
        api.throwException("Invalid date format in field '${fieldName}' for customer ${soldTo}. It should be MM/dd/yyyy.")
    }

    try {
        dateFormat.parse(value)
    } catch (Exception e) {
        api.throwException("Invalid date format in field '${fieldName}' for Sold To ${soldTo}. It should be MM/dd/yyyy.")
    }
}