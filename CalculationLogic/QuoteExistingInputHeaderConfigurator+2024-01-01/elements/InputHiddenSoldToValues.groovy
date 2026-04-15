import net.pricefx.common.api.InputType

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def value = out.GetSoldToValues

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, headerConstants.SOLD_TO_VALUES_HIDDEN_ID)
def input = entry.getFirstInput()
input.setValue(value)

return entry