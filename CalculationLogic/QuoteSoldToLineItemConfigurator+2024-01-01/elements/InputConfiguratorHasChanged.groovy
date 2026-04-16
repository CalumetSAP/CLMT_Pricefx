import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.CONFIGURATOR_HAS_CHANGED_ID)
entry.getFirstInput().setValue(true)

return entry