final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnly = !api.local.isNotFreightGroup

def entry = libs.BdpLib.UserInputs.createInputCheckbox(
        headerConstants.INCLUDE_ADDER_ID,
        headerConstants.INCLUDE_ADDER_LABEL,
        false,
        readOnly,
        false
)

return entry