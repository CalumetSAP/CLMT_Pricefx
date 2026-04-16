import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = api.local.isFreightGroup && !api.local.isPricingGroup
def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["FreightTerm"] as Map : [:]
def defaultValue = api.local.contractData?.FreightTerm as String
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.FREIGHT_TERM_ID,
            lineItemConstants.FREIGHT_TERM_LABEL,
            true,
            readOnly,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_TERM_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_TERM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry