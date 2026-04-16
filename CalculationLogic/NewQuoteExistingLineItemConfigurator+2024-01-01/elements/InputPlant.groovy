import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isPricingGroup
def readOnly = true
def options = api.local.plantOptions && !api.isInputGenerationExecution() ? api.local.plantOptions : []
def defaultValue = api.local.contractData?.Plant
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.PLANT_ID,
            lineItemConstants.PLANT_LABEL,
            required,
            readOnly,
            options as List,
            defaultValue,
            false
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PLANT_ID,
            InputType.HIDDEN,
            lineItemConstants.PLANT_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry