def priceChangePerUOMInput = api.input("PriceChangePerUOMInput")

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
} else {
    param.setValue(null)
    param.setReadOnly(true)
}

return entry
