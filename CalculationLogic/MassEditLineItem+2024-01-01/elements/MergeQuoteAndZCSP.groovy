def quote = out.LoadQuotes

return [
        OldValidFrom: quote?.PriceValidFrom,
        OldValidTo  : quote?.PriceValidTo,
        Price       : quote?.Price,
        UOM         : quote?.PricingUOM,
        Per         : quote?.Per,
        Currency    : quote?.Currency,
]