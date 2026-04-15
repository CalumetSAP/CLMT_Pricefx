if (api.isInputGenerationExecution()) return

def soldToValues = out.GetSoldToValues

if (!soldToValues) return

def customerFields = ["customerId", "name"]
def customerFilter = Filter.in("customerId", soldToValues)

def selectedCustomer = api.find("C", 0, 1, null, customerFields, customerFilter)?.find()

return selectedCustomer?.name ?: selectedCustomer?.customerId