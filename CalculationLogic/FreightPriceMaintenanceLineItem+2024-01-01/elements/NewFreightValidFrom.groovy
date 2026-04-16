def conditionTypesMap = [
        "1": "ZPFX",
        "2": "ZCSP",
        "3": "ZBPL",
]

def shouldMaintainOldDates = out.ShouldMaintainOldDates
def condType = conditionTypesMap[out.LoadQuotes.PriceType] ?: out.LoadConditionRecords.ConditionType
def freightValidFrom

if (shouldMaintainOldDates) {
    freightValidFrom = out.LoadQuotes.PriceValidFrom
} else {
    freightValidFrom = condType == "ZPFX"
            ? libs.PricelistLib.Index.getRecalculationDate(api.global.effectiveDate, out.LoadQuotes.RecalculationDate, out.LoadQuotes.RecalculationPeriod)
            : out.CalculateEffectiveDateForProtection
}

return api.attributedResult(freightValidFrom).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())