import net.pricefx.server.dto.calculation.ResultMatrix

/**
 * Convert list of item to a resultMatrix
 * @param items. List of item represented as Map
 * @param columnsMap. Map of item key to result matrix column, ex.
 *  given an item [attribute1: "value1",
 *                 attribute2: "value2"]
 *  then we need columnsMap as [attribute1: "resultMatrixColumn1",
 *                              attribute2:"resultMatrixColumn2"]
 * @param rowModificator. Closure to update a row
 * @return resultMatrix.*     */
ResultMatrix resultMatrixFromList(List<Map> items, Map columnsMap = null, Closure<Map> rowModificator = null) {
    def matrix = api.newMatrix()
    if (!items) {
        return matrix
    }
    if (!rowModificator) {
        rowModificator = { Map row -> row }
    }
    if (!columnsMap) {
        columnsMap = items.first()
                          .collectEntries { k, v -> [k, k] }
    }
    def columns = columnsMap?.values()
    matrix.addColumns(columns)
    def row
    for (it in items) {
        row = [:]
        columnsMap.each { k, v -> row.put(v, it.get(k))
        }
        row = rowModificator(row)
        matrix.addRow(row)
    }
    return matrix
}