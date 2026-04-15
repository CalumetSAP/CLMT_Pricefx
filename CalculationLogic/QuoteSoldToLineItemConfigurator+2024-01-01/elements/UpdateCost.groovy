import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def cost = out.CalculateCost && !api.isInputGenerationExecution() ? out.CalculateCost : null

def entry = api.createConfiguratorEntry(InputType.HIDDEN, lineItemConstants.COST_HIDDEN_ID)
def parameter = entry.getFirstInput()
parameter.setValue(cost)

return entry