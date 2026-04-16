return getDefaultValidFrom(out.CalculateEffectiveDateForProtection, out.LoadQuotes, out.LoadConditionRecords.ConditionType)

def getDefaultValidFrom(effectiveDateFromProtection, quotesData, conditionType) {
    if (effectiveDateFromProtection) return effectiveDateFromProtection

    def priceType = quotesData?.PriceType
    def recalculationDate = quotesData?.RecalculationDate
    def recalculationPeriod = quotesData?.RecalculationPeriod

    def conditionTypesMap = [
            "1": "ZPFX",
            "2": "ZCSP",
            "3": "ZBPL",
    ]

    def condType = conditionTypesMap[priceType] ?: conditionType

    return condType == "ZPFX" ? libs.PricelistLib.Index.getRecalculationDate(api.global.effectiveDate, recalculationDate, recalculationPeriod) : out.CalculateEffectiveDateForProtection
}