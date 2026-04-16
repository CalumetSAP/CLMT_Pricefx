final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

input = libs.BdpLib.UserInputs.createInputCheckbox(
        headerConstants.ADD_CONTRACT_LINES_ID,
        headerConstants.ADD_CONTRACT_LINES_LABEL,
        false,
        false,
        null
).getFirstInput()

return null
