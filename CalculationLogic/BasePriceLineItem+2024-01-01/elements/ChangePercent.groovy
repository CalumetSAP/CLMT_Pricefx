def changePercent
def newPrice = out.NewListPrice?.toBigDecimal()
def currentPrice = out.CurrentListPrice?.toBigDecimal()

if(newPrice && currentPrice) {
    changePercent = (newPrice - currentPrice) / currentPrice
}

return changePercent