import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputPriceType.input?.getValue() == "1"

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
               "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
               "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"]
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID,
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_LABEL,
            true,
            false,
            options
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_ID,
            InputType.HIDDEN,
            lineItemConstants.PF_CONFIGURATOR_RECALCULATION_DATE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry