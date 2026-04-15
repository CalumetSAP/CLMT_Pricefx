//def zpfxItem = out.LoadZPFX
//def quote = out.LoadQuotes
//if (zpfxItem && zpfxItem.lastUpdateDate > quote?.QuoteLastUpdate) {
//    return [
//            Price       : convertPriceBasedOnPer(zpfxItem.Amount, zpfxItem.PricingUnit, quote.Per),
//            UOM         : zpfxItem.UnitOfMeasure,
//            Per         : quote?.Per,
//            Currency    : zpfxItem.ConditionCurrency,
//    ]
//} else {
//    return [
//            Price       : quote?.Price,
//            UOM         : quote?.PricingUOM,
//            Per         : quote?.Per,
//            Currency    : quote?.Currency,
//    ]
//}
//
//def convertPriceBasedOnPer(price, pricePer, targetPer) {
//    if (price && pricePer && targetPer) {
//        return price * (targetPer/pricePer)
//    }
//    return null
//}