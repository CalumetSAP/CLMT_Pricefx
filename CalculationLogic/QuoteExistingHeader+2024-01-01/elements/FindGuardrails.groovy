if (api.isInputGenerationExecution() || (!api.local.lineItemSkus && !api.local.addedContracts)) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()
def t1 = qapi.tables().companyParameterRows(tablesConstants.GUARDRAILS)

def fields = [
        t1.key1(),
        t1.key2(),
        t1.key3(),
        t1.key4(),
        t1.key5(),
        t1.RecommendedPrice,
        t1.RecommendedPriceUOM,
        t1.PricingApprovalLevel1,
        t1.PricingApprovalLevel2,
        t1.PricingApprovalLevel3,
]

def key4
api.local.guardrailsTable = qapi.source(t1, fields)
        .stream { it.collectEntries {
            key4 = it.key3 != "*" && it.key4 != "*" ? "*" : it.key4
            [(it.key1 + "|" + it.key2 + "|" + it.key3 + "|" + key4 + "|" + it.key5): [
                    key1: it.key1,
                    key2: it.key2,
                    key3: it.key3,
                    key4: it.key4,
                    key5: it.key5,
                    attribute1: it.RecommendedPrice,
                    attribute2: it.RecommendedPriceUOM,
                    attribute3: it.PricingApprovalLevel1,
                    attribute4: it.PricingApprovalLevel2,
                    attribute5: it.PricingApprovalLevel3,
            ]]
        }}

return null