if (api.isInputGenerationExecution()) return

def selectedSoldTo = out.InputSoldTo?.getFirstInput()?.getValue()

if (!selectedSoldTo) return

def qapi = api.queryApi()
def t1 = qapi.tables().customers()

return qapi.source(t1, [t1.name()], t1.customerId().equal(selectedSoldTo)).stream { it.collect {it.name }.find() }