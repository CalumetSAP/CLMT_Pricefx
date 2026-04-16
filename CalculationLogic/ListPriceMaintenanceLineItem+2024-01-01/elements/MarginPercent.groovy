//TODO waiting definition
BigDecimal marginPercent = null
BigDecimal newCalculatedPrice = (out.NewBasePrice ?: out.NewListPrice)?.toBigDecimal()
BigDecimal cost = out.Cost?.toBigDecimal()

if (newCalculatedPrice && cost) {
    marginPercent = (newCalculatedPrice - cost) / newCalculatedPrice
}

return marginPercent