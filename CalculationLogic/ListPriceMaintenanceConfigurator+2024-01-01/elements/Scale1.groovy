import net.pricefx.common.api.InputType

def pricelistsNewItems = out.PricelistsNewItems?.getFirstInput()?.getValue()
def entry
if (pricelistsNewItems) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            "Scale1Input",
            "Scale 1",
            false,
            false
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "Scale1Input",
            InputType.HIDDEN,
            "Scale 1",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry