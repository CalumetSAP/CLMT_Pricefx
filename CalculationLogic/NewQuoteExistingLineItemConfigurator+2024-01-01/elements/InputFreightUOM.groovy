import net.pricefx.common.api.InputType

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = InputIncoterm?.input?.getValue() != "FCA" && InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def required = api.local.isPricingGroup && shouldShow
def readOnly = !api.local.isPricingGroup && !shouldShow
def options = api.local.freightUOMOptions && !api.isInputGenerationExecution() ? api.local.freightUOMOptions : []
def defaultValue = out.FindFreightValues?.FreightUOM ?: (InputPricingUOM?.input?.getValue() ?: null)
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.FREIGHT_UOM_ID,
            lineItemConstants.FREIGHT_UOM_LABEL,
            required,
            readOnly,
            options as List,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_UOM_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_UOM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry