import net.pricefx.common.api.InputType

api.logWarn("NewContractPricingDate From Previous", input.get("NewContractPricingDate"))

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, "PreviousNewContractPricingDate")

if(!entry.getFirstInput().getValue()) entry.getFirstInput().setValue(input.get("NewContractPricingDate"))

input = entry?.getFirstInput()

return entry