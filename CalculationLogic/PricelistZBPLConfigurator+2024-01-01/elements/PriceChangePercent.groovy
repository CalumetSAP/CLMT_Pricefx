import net.pricefx.common.api.InputType

def pricelists = out.Pricelists?.getFirstInput()?.getValue()

def entry
if (pricelists) {
    def priceChangePerUOMInput = out.PriceChangePerUOM?.getFirstInput()?.getValue()

    entry = libs.BdpLib.UserInputs.createInputDecimal(
            "PriceChangePercentInput",
            "Price Change %",
            false,
            true,
            "PERCENT"
    )
    def param = entry.getFirstInput()

    if (!priceChangePerUOMInput) {
        param.setReadOnly(false)
        param.setRequired(true)
    } else {
        param.setRequired(false)
        param.setValue(null)
        param.setReadOnly(true)
    }
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "PriceChangePercentInput",
            InputType.HIDDEN,
            "Price Change %",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry
