import net.pricefx.common.api.InputType

def scale2 = out.Scale2?.getFirstInput()?.getValue()
def entry
if (scale2) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            "Scale3Input",
            "Scale 3",
            false,
            false
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "Scale3Input",
            InputType.HIDDEN,
            "Scale 3",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry