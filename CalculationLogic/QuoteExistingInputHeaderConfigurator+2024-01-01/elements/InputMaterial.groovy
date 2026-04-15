final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

input = libs.BdpLib.UserInputs.createInputProductGroup(
        headerConstants.MATERIAL_ID,
        headerConstants.MATERIAL_LABEL,
        false,
        false,
        headerConstants.MATERIAL_URL,
        null
).getFirstInput()

return null
