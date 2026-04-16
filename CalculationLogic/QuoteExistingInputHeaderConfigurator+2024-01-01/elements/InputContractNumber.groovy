final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def entry = libs.BdpLib.UserInputs.createInputOptions(
        headerConstants.CONTRACT_NUMBER_ID,
        headerConstants.CONTRACT_NUMBER_LABEL,
        true,
        false,
        null,
        []
)

input = entry.getFirstInput()

if (input.getValue() && !InputShipTo.input?.getValue() && !InputSoldTo.input?.getValue()) {
    out.InputDoNotFilterHidden?.getFirstInput()?.setValue(true)
}

if (!input.getValue() && !InputShipTo.input?.getValue() && !InputSoldTo.input?.getValue()) {
    out.InputDoNotFilterHidden?.getFirstInput()?.setValue(false)
}

if (!input.getValue() && (InputShipTo.input?.getValue() || InputSoldTo.input?.getValue())) {
    out.InputDoNotFilterHidden?.getFirstInput()?.setValue(false)
}

return null