import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def defaultValue = api.local.shouldUseDefault ? api.local.contractData?.ThirdPartyCustomer as String : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.THIRD_PARTY_CUSTOMER_ID,
            lineItemConstants.THIRD_PARTY_CUSTOMER_LABEL,
            false,
            false,
            defaultValue
    ).getFirstInput()
    input?.setConfigParameter("maxLength", 35)
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.THIRD_PARTY_CUSTOMER_ID,
            InputType.HIDDEN,
            lineItemConstants.THIRD_PARTY_CUSTOMER_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry