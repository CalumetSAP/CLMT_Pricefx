final constants = libs.PricelistLib.Constants
def pl = api.currentItem()
def headerTypeUniqueName = pl?.headerTypeUniqueName

api.local.plItems = libs.PricelistLib.Common.getAllPLItems(pl?.id)

api.local.isMassEdit = headerTypeUniqueName == constants.MASS_EDIT_PL_TYPE
api.local.isPricingFormula = headerTypeUniqueName == constants.PRICING_FORMULA_PL_TYPE
api.local.isListPriceZLIS = headerTypeUniqueName == constants.LIST_PRICE_ZLIS_PL_TYPE
api.local.isPricelistZBPL = headerTypeUniqueName == constants.PRICE_LIST_ZBPL_PL_TYPE

return null