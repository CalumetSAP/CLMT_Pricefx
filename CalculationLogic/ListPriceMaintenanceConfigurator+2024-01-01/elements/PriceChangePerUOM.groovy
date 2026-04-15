import java.math.RoundingMode

def priceChangePercentInput = input.PriceChangePercentInput

def entry = libs.BdpLib.UserInputs.createInputDecimal(
        "PriceChangePerUOMInput",
        "Price Change per UOM \$",
        false,
        true,
        "###.0000"
)
def param = entry.getFirstInput()

if (!priceChangePercentInput) {
    param.setReadOnly(false)
    param.setRequired(true)
} else {
    param.setRequired(false)
    param.setValue(null)
    param.setReadOnly(true)
}

if(param.getValue()){
    param.setValue(param.getValue()?.toBigDecimal()?.setScale(4, RoundingMode.HALF_UP))
}

return entry