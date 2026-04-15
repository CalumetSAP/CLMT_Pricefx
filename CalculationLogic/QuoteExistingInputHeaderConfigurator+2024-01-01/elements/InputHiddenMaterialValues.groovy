import net.pricefx.common.api.InputType

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def value = out.GetMaterialValues

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, headerConstants.MATERIAL_VALUES_HIDDEN_ID)
def input = entry.getFirstInput()
input.setValue(value)

return entry