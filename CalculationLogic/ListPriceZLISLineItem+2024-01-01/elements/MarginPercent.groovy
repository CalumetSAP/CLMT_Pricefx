BigDecimal marginPercent = null
BigDecimal newCalculatedPrice = out.NewListPrice?.toBigDecimal()
BigDecimal cost = out.Cost?.toBigDecimal()

if (newCalculatedPrice && cost) {
    marginPercent = (newCalculatedPrice - cost) / newCalculatedPrice
}

return marginPercent