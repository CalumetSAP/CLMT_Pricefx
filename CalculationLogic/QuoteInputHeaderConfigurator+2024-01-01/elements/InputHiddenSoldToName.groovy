import net.pricefx.common.api.InputType

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def value = out.FindSoldTo ?: null

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, headerConstants.SOLD_TO_HIDDEN_ID)
entry.getFirstInput().setValue(value)

return entry