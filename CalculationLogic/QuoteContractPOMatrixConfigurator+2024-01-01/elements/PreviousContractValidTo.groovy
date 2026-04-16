import net.pricefx.common.api.InputType

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, "PreviousContractValidTo")

if(!entry.getFirstInput().getValue()) entry.getFirstInput().setValue(input.get("ContractValidTo"))

input = entry?.getFirstInput()

return entry