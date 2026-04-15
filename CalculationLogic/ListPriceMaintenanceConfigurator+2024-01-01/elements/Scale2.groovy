import net.pricefx.common.api.InputType

def scale1 = out.Scale1?.getFirstInput()?.getValue()
def entry
if (scale1) {
    entry = libs.BdpLib.UserInputs.createInputDecimal(
            "Scale2Input",
            "Scale 2",
            false,
            false
    )
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            "Scale2Input",
            InputType.HIDDEN,
            "Scale 2",
            false,
            true
    )
    entry.getFirstInput().setValue(null)
}

return entry