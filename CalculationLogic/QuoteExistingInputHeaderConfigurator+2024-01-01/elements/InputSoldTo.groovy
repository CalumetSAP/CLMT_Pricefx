final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

input = libs.BdpLib.UserInputs.createInputCustomerGroup(
        headerConstants.SOLD_TO_ID,
        headerConstants.SOLD_TO_LABEL,
        false,
        false,
        headerConstants.SOLD_TO_URL,
        null
).getFirstInput()

return null
