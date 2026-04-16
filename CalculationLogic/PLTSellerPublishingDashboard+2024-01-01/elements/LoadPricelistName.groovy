final tablesConstants = libs.QuoteConstantsLibrary.Tables
final constants = libs.DashboardConstantsLibrary.PLTDashboard

def pricelist = out.Filters?.get(constants.PRICELIST_INPUT_KEY)

def filters = Filter.and(
        Filter.equal("lookupTable.name", tablesConstants.PRICELIST),
        Filter.equal("lookupTable.status", "Active"),
        Filter.equal("name", pricelist)
)
def fields = ["name", "attribute2"]

def name = api.find("MLTV", 0, 1, null, fields, filters)?.find()?.attribute2

return name