if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return
if (out.InputPriceProtection?.getFirstInput()?.getValue() != "1" &&
        out.InputPriceProtection?.getFirstInput()?.getValue() != "2") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def readOnly = !api.local.isNotFreightGroup
def numberOfDays = out.FindExclusion?.attribute1?.toString()
def defaultValue = numberOfDays?.isNumber() ? numberOfDays?.toInteger() : null

def entry = libs.BdpLib.UserInputs.createInputNumber(
        lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID,
        lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_LABEL,
        true,
        readOnly,
        defaultValue
)

return entry