BigDecimal change = null
BigDecimal newPrice = out.NewBasePrice?.toBigDecimal()
BigDecimal currentPrice = out.CurrentBasePrice?.toBigDecimal()

if (newPrice != null && currentPrice != null) {
    change = newPrice - currentPrice
} else if (newPrice != null) {
    change = newPrice
} else if (currentPrice != null) {
    change = -currentPrice
}

return change