import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def priceType = InputPriceType.input?.getValue()

def havePermissions = api.local.isPricingGroup
def required = api.local.isPricingGroup && priceType != "4"
def readonly = !required
def defaultValue = api.local.contractData?.Per as BigDecimal ?: BigDecimal.ONE
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDecimal(
            lineItemConstants.PER_ID,
            lineItemConstants.PER_LABEL,
            required,
            readonly,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.PER_ID,
            InputType.HIDDEN,
            lineItemConstants.PER_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

if(!input?.getValue()) input?.setValue(BigDecimal.ONE)

return entry