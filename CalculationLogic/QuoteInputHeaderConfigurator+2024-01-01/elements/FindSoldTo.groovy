if (api.isInputGenerationExecution()) return

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()

if (!selectedSoldTo) return

def customerFields = ["customerId", "name"]
def customerFilter = Filter.equal("customerId", selectedSoldTo)

def selectedCustomer = api.find("C", 0, 1, null, customerFields, customerFilter)?.find()

return selectedCustomer?.name