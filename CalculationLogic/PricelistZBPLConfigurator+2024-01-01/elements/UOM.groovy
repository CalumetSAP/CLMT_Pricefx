import net.pricefx.common.api.InputType

def pricelists = out.Pricelists?.getFirstInput()?.getValue()

def entry
if (pricelists) {
    def priceChangePerUOM = out.PriceChangePerUOM?.getFirstInput()?.getValue()

    def uomList = ["LB", "KG", "UG6"]
    def options = [*uomList]

    entry = libs.BdpLib.UserInputs.createInputOption(
            "UOMInput",
            "UOM",
            false,
            true,
            options
    )
    def param = entry.getFirstInput()

    if (priceChangePerUOM) {
        param.setRequired(true)
        param.setReadOnly(false)
    } else {
        param.setRequired(false)
        param.setReadOnly(true)
        param.setValue(null)
    }
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "UOMInput",
            InputType.HIDDEN,
            "UOM",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry