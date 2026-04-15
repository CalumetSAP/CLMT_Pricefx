import net.pricefx.common.api.FieldFormatType
import net.pricefx.server.dto.calculation.ResultMatrix

/**
 * Applies defined formatting to each of the result matrix columns.
 * @param matrix result matrix to whose columns the formatting will be applied
 * @param usedColumns columns used in particular table
 */
void applyResultMatrixFormats(ResultMatrix matrix, List usedColumns) {
    if (!matrix) {
        return
    }

    usedColumns.each { Map column ->
        setMatrixColumnFormat(matrix, column.LABEL, column.RESULT_MATRIX)
    }
}

/**
 * Sets the format for a given column name, in case of missing data no format is applied.
 * @param matrix result matrix to whose columns the formatting will be applied
 * @param columnName name of the column to format
 * @param format FieldFormatType for the column
 */
void setMatrixColumnFormat(ResultMatrix matrix, String columnName, FieldFormatType format) {
    if (!matrix || !columnName || !format) {
        return
    }

    matrix.setColumnFormat(columnName, format)
}

/**
 * Retrieves the percentage ratio or decimal value for a given value and percentage display flag.
 * if the percentage display flag is true, convert the value to percentage ration
 * else convert keep the current value
 * @param value value for the calculation
 * @param percentageDisplay percentage base for the calculation
 * @return corresponding value by percentage display flag
 */
BigDecimal getPercentageOrDecimalValueForDataTab(BigDecimal value, boolean percentageDisplay) {
    return percentageDisplay && value ? value / 100 : value
}
