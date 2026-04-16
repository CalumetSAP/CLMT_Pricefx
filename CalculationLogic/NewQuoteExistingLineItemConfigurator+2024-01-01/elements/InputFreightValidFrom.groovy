import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = InputIncoterm?.input?.getValue() != "FCA" && InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isPricingGroup && shouldShow
def readOnly = !api.local.isPricingGroup && !shouldShow
def defaultValue = out.FindFreightValues?.FreightValidFrom ?: (api.local.validFromDate ?: null)
api.local.defaultFreightValidFrom = defaultValue
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDate(
            lineItemConstants.FREIGHT_VALID_FROM_ID,
            lineItemConstants.FREIGHT_VALID_FROM_LABEL,
            required,
            readOnly,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_VALID_FROM_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_VALID_FROM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry