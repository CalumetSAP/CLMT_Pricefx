import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

def entry = api.createConfiguratorEntry()

def priceTypeNewValue = InputPriceType?.input?.getValue()

ContextParameter priceTypePreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceType?.input?.getName() + "Previous")

def priceTypeCallback = { v ->
    api.local.priceTypeHasChanged = true
}

if (priceTypeNewValue != priceTypePreviousValue?.getValue()) {
    priceTypePreviousValue?.setValue(priceTypeNewValue)
    priceTypeCallback(priceTypeNewValue)
}

return entry