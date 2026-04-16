import net.pricefx.common.api.InputType
import java.math.RoundingMode

def pricelists = out.Pricelists?.getFirstInput()?.getValue()

def entry
if (pricelists) {
    def priceChangePercentInput = input.PriceChangePercentInput

    entry = libs.BdpLib.UserInputs.createInputDecimal(
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

    if (param.getValue()) {
        param.setValue(param.getValue()?.toBigDecimal()?.setScale(4, RoundingMode.HALF_UP))
    }
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "PriceChangePerUOMInput",
            InputType.HIDDEN,
            "Price Change per UOM \$",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry