if(api.isInputGenerationExecution()) return // if saving logic from navigator, prevent addConditionRecordAction throw an exception (error because of a logWarn)
api.logInfo("AGUS - TEST")

conditionRecordHelper.addOrUpdate([
        key1: "ZCSP",
        key2: "US30",
        key3: "40022312",
        key4: "10",
        key5: "300079002007",
        validFrom: "2025-02-28",
        validTo: "2026-02-28",
        conditionRecordSetId: out.LoadConditionRecordSetMap["A904"],
        conditionValue: 11,
        unitOfMeasure: "UG6",
        currency: "US3",
        integrationStatus: 1,
        attribute1: "2662.Q",
        attribute5: "X",
])

conditionRecordHelper.addConditionRecordAction().setCalculate(true)