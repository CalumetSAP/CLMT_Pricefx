def conditionTypesMap = [
        "1": "ZPFX",
        "2": "ZCSP",
        "3": "ZBPL",
]

return conditionTypesMap[out.LoadQuotes.PriceType] ?: out.LoadConditionRecords.ConditionType
