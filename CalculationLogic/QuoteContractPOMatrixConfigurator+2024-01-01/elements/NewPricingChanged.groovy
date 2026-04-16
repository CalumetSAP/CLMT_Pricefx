import net.pricefx.common.api.InputType

api.logWarn("NewContractPricingDate", NewContractPricingDate.input.getValue())
def NewContractPricingDateValue = NewContractPricingDate.input.getValue()
api.logWarn("PreviousNewContractPricingDate", PreviousNewPricingDate.input.getValue())
def PreviousNewPricingDateValue = PreviousNewPricingDate.input.getValue()

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, "NewPricingChanged")
if(!entry.getFirstInput().getValue()) entry.getFirstInput().setValue(false)

if(NewContractPricingDateValue != PreviousNewPricingDateValue) {
    entry.getFirstInput().setValue(true)
    PreviousNewPricingDate.input.setValue(input.get("NewContractPricingDate"))
}

//if(NewContractPricingDateValue == PreviousNewPricingDateValue) {
//    entry.getFirstInput().setValue(false)
//}

api.logWarn( "Pricing Date Changed", entry.getFirstInput().getValue())

return entry