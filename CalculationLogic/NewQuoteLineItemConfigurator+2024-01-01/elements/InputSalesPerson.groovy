import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def options = api.local.salesPersonOptions && !api.isInputGenerationExecution() ? api.local.salesPersonOptions : []
def defaultValue = options?.size() == 1 ? options?.find() : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.SALES_PERSON_ID,
            lineItemConstants.SALES_PERSON_LABEL,
            true,
            false,
            options as List,
            defaultValue,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SALES_PERSON_ID,
            InputType.HIDDEN,
            lineItemConstants.SALES_PERSON_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry