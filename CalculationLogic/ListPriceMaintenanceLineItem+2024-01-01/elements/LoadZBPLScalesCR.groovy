String lineNumber = api.local.lineNumber

if (out.LoadZBPLCR) {
    List<String> lineNumbers = libs.PricelistLib.Constants.ZBPL_SCALES_LINE_NUMBERS.clone()

    Map<String, String> lineNumberAndScale = new HashMap<>()
    String lineNumberAux
    out.LoadZBPLCR.attribute2?.split("\\|")?.each {
        if (lineNumbers.size() > 0) {
            lineNumberAux = lineNumbers.remove(0)
            lineNumberAndScale.put(lineNumberAux, it)
        }
    }

    List<String> qtyAndConditionRate = lineNumberAndScale.get(lineNumber)?.split("=")?.toList()
    if (qtyAndConditionRate?.size() == 2) {
        return [
                ScaleQuantity: qtyAndConditionRate[0]?.toBigDecimal() ?: null,
                ConditionRate: qtyAndConditionRate[1]?.toBigDecimal() ?: null
        ]
    }
}

return [:]