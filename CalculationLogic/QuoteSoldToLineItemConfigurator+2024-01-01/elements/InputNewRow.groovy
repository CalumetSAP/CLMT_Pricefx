import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, "NewRowInput")
if (entry.getFirstInput().getValue() == null) entry.getFirstInput().setValue(false)

return entry