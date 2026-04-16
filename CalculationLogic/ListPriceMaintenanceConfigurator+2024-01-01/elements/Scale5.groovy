import net.pricefx.common.api.InputType

def scale4 = out.Scale4?.getFirstInput()?.getValue()
def entry
if (scale4) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            "Scale5Input",
            "Scale 5",
            false,
            false
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "Scale5Input",
            InputType.HIDDEN,
            "Scale 5",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry