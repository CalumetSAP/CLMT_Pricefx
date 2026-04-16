import net.pricefx.common.api.InputType

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnly = !api.local.isNotFreightGroup

api.local.inputSoldToHasChange = false
def callback = { value -> api.local.inputSoldToHasChange = true }

def entry = libs.BdpLib.UserInputs.createInputCustomer(
        headerConstants.SOLD_TO_ID,
        headerConstants.SOLD_TO_LABEL,
        true,
        readOnly,
        headerConstants.SOLD_TO_URL,
        [ShouldUseBlockFilter: true],
        callback
)

return entry
