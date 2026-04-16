def amount = api.global.ListPriceMap?.get(api.local.material)?.Amount
def currency = api.global.ListPriceMap?.get(api.local.material)?.Currency
def currencyConversion = currency == '*' ? BigDecimal.ONE : out.LoadCurrencyConversion?.get(currency)

def uomListPrice = api.global.ListPriceMap?.get(api.local.material)?.UOM
def pricingUOM = out.UOM

def conversionFactor = libs.PricelistLib.Conversion.getConversionFactor(api.local.material, uomListPrice, pricingUOM, api.global.uomConversion, api.global.globalUOMConversion)?.toBigDecimal()

amount = amount && currencyConversion && conversionFactor ? amount * conversionFactor * currencyConversion : null

return amount