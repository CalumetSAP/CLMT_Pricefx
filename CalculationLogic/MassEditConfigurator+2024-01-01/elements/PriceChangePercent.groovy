def priceChangePerUOMInput = out.PriceChangePerUOM?.getFirstInput()?.getValue()

def entry = libs.BdpLib.UserInputs.createInputDecimal(
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

return entry
