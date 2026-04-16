if (out.Index3) {
    Map index3Calculation = api.local.index3Calculation
    if (index3Calculation?.conversionAlertMsgs) {
        api.criticalAlert(index3Calculation?.conversionAlertMsgs?.join("; ") as String)
    }
    return index3Calculation?.indexValueInAdderUOM
}

return null