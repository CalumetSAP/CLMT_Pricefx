BigDecimal changePercent = null
BigDecimal newPrice = out.NewPrice?.toBigDecimal()
BigDecimal currentPrice = out.CurrentPrice?.toBigDecimal()

if(newPrice && currentPrice) {
    changePercent = (newPrice - currentPrice) / currentPrice
}

return changePercent