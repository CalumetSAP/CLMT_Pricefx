if (api.global.salesOrgs) return

final tablesConstants = libs.QuoteConstantsLibrary.Tables

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows(tablesConstants.SALES_ORG)

api.global.salesOrgs = qapi.source(t1, [t1.key1()], t1."Show on List Price Maintenance".equal(true)).stream {it.collect { it.key1 }} ?: []