BigDecimal amount = out.ZLISMerged.Price?.toBigDecimal()

//String currency = out.ZLISMerged.Currency

//BigDecimal currencyConversion = currency == '*' ? BigDecimal.ONE : out.LoadCurrencyConversion?.get(currency)
//amount = amount && currencyConversion ? amount * currencyConversion : null

return amount