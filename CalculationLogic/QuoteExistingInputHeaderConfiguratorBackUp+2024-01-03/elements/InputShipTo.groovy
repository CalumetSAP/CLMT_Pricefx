final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = out.FindShipToOptions ? ["Select All", *(out.FindShipToOptions?.values() as List)] : []

input = libs.BdpLib.UserInputs.createInputOptions(
        headerConstants.SHIP_TO_ID,
        headerConstants.SHIP_TO_LABEL,
        false,
        false,
        null,
        options
).getFirstInput()

if (!input.getValue()?.every { it in options }) {
    input.setValue(null)
}

if(input.getValue()?.any{ it  == "Select All" }) {
    input.setValue(out.FindShipToOptions?.values() as List)
}

return null