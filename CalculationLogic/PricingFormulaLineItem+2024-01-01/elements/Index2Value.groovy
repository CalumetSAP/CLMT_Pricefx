if (out.Index2) {
    Map index2Calculation = api.local.index2Calculation
    if (index2Calculation?.conversionAlertMsgs) {
        api.criticalAlert(index2Calculation?.conversionAlertMsgs?.join("; ") as String)
    }
    return index2Calculation?.indexValueInAdderUOM
}

return null