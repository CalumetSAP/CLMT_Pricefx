import net.pricefx.common.api.InputType

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, libs.QuoteConstantsLibrary.LineItem.DO_NOT_FILTER_HIDDEN_ID)

return entry