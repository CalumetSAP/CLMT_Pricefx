def quotesRow = out.LoadQuotes

// If there is no index selected and the user overrides the index percent => remove that index percent override
if (!quotesRow?.IndexNumberOne && !api.getManualOverride("Index1") && api.getManualOverride("Index1Percent") != null) {
    api.removeManualOverride("Index1Percent")
}
if (!quotesRow?.IndexNumberTwo && !api.getManualOverride("Index2") && api.getManualOverride("Index2Percent") != null) {
    api.removeManualOverride("Index2Percent")
}
if (!quotesRow?.IndexNumberThree && !api.getManualOverride("Index3") && api.getManualOverride("Index3Percent") != null) {
    api.removeManualOverride("Index3Percent")
}

if (api.getManualOverride("NewPrice")) {
    api.removeManualOverride("Adder")
}