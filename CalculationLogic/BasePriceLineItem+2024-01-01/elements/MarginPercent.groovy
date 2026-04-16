def marginPercent
def newPrice = out.NewListPrice?.toBigDecimal()
def cost = out.Cost?.toBigDecimal()

if(newPrice && cost) {
    marginPercent = (newPrice - cost) / newPrice
}

return marginPercent