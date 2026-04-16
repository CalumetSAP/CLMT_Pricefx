import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = api.local.isFreightGroup && !api.local.isPricingGroup
def defaultValue = api.local.contractData?.NamedPlace as String ?: null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.NAMED_PLACE_ID,
            lineItemConstants.NAMED_PLACE_LABEL,
            false,
            readOnly,
            defaultValue
    ).getFirstInput()
    input?.setConfigParameter("maxLength", 25)
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.NAMED_PLACE_ID,
            InputType.HIDDEN,
            lineItemConstants.NAMED_PLACE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

return entry