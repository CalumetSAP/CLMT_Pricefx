//TODO move to a PricelistLib. DUPLICATED (4)
BigDecimal change = null
BigDecimal newPrice = out.NewListPrice?.toBigDecimal()
BigDecimal currentPrice = out.CurrentListPrice?.toBigDecimal()

if (newPrice != null && currentPrice != null) {
    change = newPrice - currentPrice
} else if (newPrice != null) {
    change = newPrice
} else if (currentPrice != null) {
    change = -currentPrice
}

return change