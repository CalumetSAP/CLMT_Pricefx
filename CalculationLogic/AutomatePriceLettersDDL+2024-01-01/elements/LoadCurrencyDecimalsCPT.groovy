if (api.isInputGenerationExecution()) return

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("CurrencyDecimals")
def fields = [
        t1.key1().as("Name"),
        t1.NumberOfDecimals
]

api.global.currencyDecimals = qapi.source(t1, fields).stream { it.collectEntries {
    [(it.Name): it.NumberOfDecimals]
}}

return null