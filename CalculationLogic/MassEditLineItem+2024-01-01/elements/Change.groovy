BigDecimal change = null
BigDecimal newPrice = out.NewPrice?.toBigDecimal()
BigDecimal currentPrice = out.CurrentPrice?.toBigDecimal()

if (newPrice != null && currentPrice != null) {
    change = newPrice - currentPrice
} else if (newPrice != null) {
    change = newPrice
} else if (currentPrice != null) {
    change = -currentPrice
}

return change