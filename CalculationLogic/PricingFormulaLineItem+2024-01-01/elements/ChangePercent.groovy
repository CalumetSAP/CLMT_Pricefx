BigDecimal oldPrice = out.CurrentPrice?.toBigDecimal()
BigDecimal newPrice = out.NewPrice?.toBigDecimal()

if (oldPrice && newPrice) {
    return (newPrice / oldPrice) - 1
}

return null