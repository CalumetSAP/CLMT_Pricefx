if (api.isInputGenerationExecution()) return

def soldToValues = out.GetSoldToValues

if (!soldToValues) return

def qapi = api.queryApi()
def t1 = qapi.tables().customers()

def selectedCustomer = qapi.source(t1, [t1.customerId(), t1.name()], t1.customerId().in(soldToValues))
        .take(1)
        .stream { it.collect() }?.find()

return selectedCustomer?.name ?: selectedCustomer?.customerId