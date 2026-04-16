def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
def formatType = "#,##0." + "0" * numberOfDecimals?.toInteger()

InputPrice?.entry?.getFirstInput()?.setConfigParameter("formatType", formatType)
InputRecommendedPrice?.input?.setConfigParameter("formatType", formatType)