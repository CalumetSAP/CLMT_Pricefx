if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

final calculations = libs.QuoteLibrary.Calculations

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows("ApproversTable")
def fields = [
        t1.key1(),
        t1.key2(),
        t1.key3(),
        t1.key4(),
        t1.key5(),
        t1.key6(),
        t1.Approver
]

def data = qapi.source(t1, fields).stream {it.collect {
    return [
            key1      : it.key1,
            key2      : it.key2,
            key3      : it.key3,
            key4      : it.key4,
            key5      : it.key5,
            key6      : it.key6,
            attribute3: it.Approver,
    ]
}}

return calculations.groupTotalApproversData(data)