if (out.InputPriceProtection?.getFirstInput()?.getValue() == "4") return
if (out.InputPriceProtection?.getFirstInput()?.getValue() != "1" &&
        out.InputPriceProtection?.getFirstInput()?.getValue() != "2") return

final lineItem = libs.QuoteConstantsLibrary.LineItem

def readOnly = api.local.readOnly ? true : false//api.local.exclusionData ? true : false

def entry
if (api.local.exclusionData?.NumberOfDays) {
    entry = libs.BdpLib.UserInputs.createInputNumber(
            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID,
            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_LABEL,
            true,
            readOnly,
            api.local.exclusionData?.NumberOfDays as Integer
    )
} else {
    entry = libs.BdpLib.UserInputs.createInputNumber(
            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_ID,
            lineItem.PP_CONFIGURATOR_NUMBER_OF_DAYS_LABEL,
            true,
            readOnly
    )
}


return entry