def currentListPrice = out.CurrentListPrice?.toBigDecimal() ?: BigDecimal.ZERO
def discount = out.Discount?.toBigDecimal() ?: BigDecimal.ZERO

return currentListPrice * (1-discount)