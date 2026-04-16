def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
def formatType = "#,##0." + "0" * numberOfDecimals?.toInteger()

PFAdder?.entry?.getFirstInput()?.setConfigParameter("formatType", formatType)
InputPrice?.entry?.getFirstInput()?.setConfigParameter("formatType", formatType)
InputDeliveredPrice?.entry?.getFirstInput()?.setConfigParameter("formatType", formatType)
InputRecommendedPrice?.input?.setConfigParameter("formatType", formatType)
InputFreightAmount?.entry?.getFirstInput()?.setConfigParameter("formatType", formatType)