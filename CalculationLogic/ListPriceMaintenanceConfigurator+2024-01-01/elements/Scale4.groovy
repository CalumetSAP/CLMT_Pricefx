import net.pricefx.common.api.InputType

def scale3 = out.Scale3?.getFirstInput()?.getValue()
def entry
if (scale3) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            "Scale4Input",
            "Scale 4",
            false,
            false
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "Scale4Input",
            InputType.HIDDEN,
            "Scale 4",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry