import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

def havePermissions = api.local.isSalesGroup || api.local.isPricingGroup || api.local.isFreightGroup

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def readOnly = api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup
def options = api.local.incotermOptions && !api.isInputGenerationExecution() ? api.local.incotermOptions as Map : [:]
def defaultValue = options?.size() == 1 ? options?.keySet()?.find() : null
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputOption(
            lineItemConstants.INCO_TERM_ID,
            lineItemConstants.INCO_TERM_LABEL,
            true,
            readOnly,
            defaultValue,
            options as Map
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.INCO_TERM_ID,
            InputType.HIDDEN,
            lineItemConstants.INCO_TERM_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
}

if (havePermissions) {
    def freightTerm = InputFreightTerm?.input?.getValue()
    ContextParameter freightTermHidden = out.FreightTermHidden?.getFirstInput()
    def freightTermHiddenValue = freightTermHidden?.getValue()
    if (freightTermHiddenValue != freightTerm) {
        freightTermHidden?.setValue(freightTerm)
        def updatedValue = null
        switch (freightTerm) {
            case "1":
                updatedValue = "FCA"
                break
            case "2":
                updatedValue = "CPT"
                break
            case "3":
                updatedValue = "DAP"
                break
            case "4":
                updatedValue = "DAP"
                break
        }
        input.setValue(updatedValue)
    }
}

return entry