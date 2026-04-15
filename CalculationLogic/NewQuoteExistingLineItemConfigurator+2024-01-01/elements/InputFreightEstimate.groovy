import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup
def shouldShow = InputIncoterm?.input?.getValue() != "FCA" && "BULK".equalsIgnoreCase(InputMaterialPackageStyle?.input?.getValue())

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def defaultValue = false
def readOnly = !(api.local.isPricingGroup)
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputCheckbox(
            lineItemConstants.FREIGHT_ESTIMATE_ID,
            lineItemConstants.FREIGHT_ESTIMATE_LABEL,
            false,
            readOnly,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_ESTIMATE_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_ESTIMATE_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry