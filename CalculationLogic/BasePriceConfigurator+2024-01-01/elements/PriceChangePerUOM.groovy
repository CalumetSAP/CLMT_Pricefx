def priceChangePercentInput = out.PriceChangePercent?.getFirstInput()?.getValue()

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
} else {
    param.setValue(null)
    param.setReadOnly(true)
}

if(param.getValue()){
    param.setValue(param.getValue()?.toBigDecimal()?.setScale(4, BigDecimal.ROUND_HALF_UP))
}

return entry