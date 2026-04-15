import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.C4C_UOM_HIDDEN_ID)

def value = entry.getFirstInput().getValue()
if (value) {
    InputPricingUOM?.input?.setValue(api.local.c4cUOM?.get(value))
}

return entry