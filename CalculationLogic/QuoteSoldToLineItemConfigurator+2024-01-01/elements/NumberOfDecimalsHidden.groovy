import net.pricefx.common.api.InputType

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, libs.QuoteConstantsLibrary.LineItem.NUMBER_OF_DECIMALS_HIDDEN_ID)

return entry