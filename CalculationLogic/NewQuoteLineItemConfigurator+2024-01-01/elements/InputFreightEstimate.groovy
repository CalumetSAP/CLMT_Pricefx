import net.pricefx.common.api.InputType

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup
def shouldShow = InputIncoterm?.input?.getValue() != "FCA" && !api.local.isSoldToOnly && InputSalesShippingMethod?.input?.getValue() != "Packaged" && "BULK".equalsIgnoreCase(InputMaterialPackageStyle?.input?.getValue())
api.local.shouldShowFreightFields = shouldShow

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = !(api.local.isSalesGroup || api.local.isPricingGroup)
def entry = null
if (havePermissions && shouldShow) {
    input = libs.BdpLib.UserInputs.createInputCheckbox(
            lineItemConstants.FREIGHT_ESTIMATE_ID,
            lineItemConstants.FREIGHT_ESTIMATE_LABEL,
            false,
            readOnly,
            false
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