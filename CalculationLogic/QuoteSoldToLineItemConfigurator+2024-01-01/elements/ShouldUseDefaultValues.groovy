import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.SHOULD_USE_DEFAULT_VALUES_ID)
if (entry.getFirstInput().getValue() == null) entry.getFirstInput().setValue(true)

api.local.shouldUseDefault = entry.getFirstInput().getValue()

return entry