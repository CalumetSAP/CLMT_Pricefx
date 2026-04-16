import net.pricefx.common.api.InputType

def ContractValidToValue = ContractValidTo.input.getValue()
def PreviousContractValidToValue = PreviousContractValidTo.input.getValue()

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, "ContractValidToChanged")
if(!entry.getFirstInput().getValue()) entry.getFirstInput().setValue(false)

if(ContractValidToValue != PreviousContractValidToValue) {
    entry.getFirstInput().setValue(true)
    PreviousNewPricingDate.input.setValue(input.get("ContractValidTo"))
}

api.logWarn( "Pricing Date Changed", entry.getFirstInput().getValue())

return entry