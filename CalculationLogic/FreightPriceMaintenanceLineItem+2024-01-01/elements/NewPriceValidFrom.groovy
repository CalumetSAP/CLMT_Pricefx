def conditionTypesMap = [
        "1": "ZPFX",
        "2": "ZCSP",
        "3": "ZBPL",
]

def shouldMaintainOldDates = out.ShouldMaintainOldDates
def condType = conditionTypesMap[out.LoadQuotes.PriceType] ?: out.LoadConditionRecords.ConditionType
def priceValidFrom

if (shouldMaintainOldDates) {
    priceValidFrom = out.LoadQuotes.PriceValidFrom
} else {
    priceValidFrom = condType == "ZPFX"
            ? libs.PricelistLib.Index.getRecalculationDate(api.global.effectiveDate, out.LoadQuotes.RecalculationDate, out.LoadQuotes.RecalculationPeriod)
            : out.CalculateEffectiveDateForProtection
}

return api.attributedResult(priceValidFrom).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())