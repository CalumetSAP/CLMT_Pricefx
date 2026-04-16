def zcspItem = out.LoadZCSP
def quote = out.LoadQuotes
if (zcspItem && zcspItem.lastUpdateDate > quote?.QuoteLastUpdate) {
    return [
            OldValidFrom: zcspItem.ValidFrom,
            OldValidTo  : zcspItem.ValidTo,
            Price       : convertPriceBasedOnPer(zcspItem.Amount, zcspItem.PricingUnit, quote.Per),
            UOM         : zcspItem.UnitOfMeasure,
            Per         : quote?.Per,
            Currency    : zcspItem.ConditionCurrency,
    ]
} else {
    return [
            OldValidFrom: quote?.PriceValidFrom,
            OldValidTo  : quote?.PriceValidTo,
            Price       : quote?.Price,
            UOM         : quote?.PricingUOM,
            Per         : quote?.Per,
            Currency    : quote?.Currency,
    ]
}

def convertPriceBasedOnPer(price, pricePer, targetPer) {
    if (price && pricePer && targetPer) {
        return price * (targetPer/pricePer)
    }
    return null
}