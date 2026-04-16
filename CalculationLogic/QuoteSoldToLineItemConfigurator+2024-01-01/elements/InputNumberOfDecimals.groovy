import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def required = api.local.isPricingGroup && priceType != "4"
def readOnly = !required || priceType == "3"
def options = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["NumberOfDecimals"] as Map : [:]
def defaultValue = api.local.contractData?.NumberofDecimals as String
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.NUMBER_OF_DECIMALS_ID,
            lineItemConstants.NUMBER_OF_DECIMALS_LABEL,
            required,
            readOnly,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.NUMBER_OF_DECIMALS_ID,
            InputType.HIDDEN,
            lineItemConstants.NUMBER_OF_DECIMALS_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

if (priceType == "3") input.setValue("2")

return entry