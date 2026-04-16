import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

def entry = api.createConfiguratorEntry()

def modeOfTransportationNewValue = out.InputModeOfTransportation?.getFirstInput()?.getValue()

ContextParameter modeOfTransportationPreviousValue = entry.createParameter(InputType.HIDDEN, out.InputModeOfTransportation?.getFirstInput()?.getName() + "Previous")

def callback = { v ->
    api.local.modeOfTransportationHasChanged = true
}

if (modeOfTransportationNewValue != modeOfTransportationPreviousValue?.getValue()) {
    modeOfTransportationPreviousValue?.setValue(modeOfTransportationNewValue)
    callback(modeOfTransportationNewValue)
}

return entry