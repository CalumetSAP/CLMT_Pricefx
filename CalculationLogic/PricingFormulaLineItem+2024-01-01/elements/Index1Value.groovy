if (out.Index1) {
    Map index1Calculation = api.local.index1Calculation
    if (index1Calculation?.conversionAlertMsgs) {
        api.criticalAlert(index1Calculation?.conversionAlertMsgs?.join("; ") as String)
    }
    return index1Calculation?.indexValueInAdderUOM
}

return null