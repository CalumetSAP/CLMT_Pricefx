List<String> warnings = []

def fixedAdder = out.FixedAdder
def oldAdder = out.OldAdder
def newAdder = out.NewAdder

if (fixedAdder == "Y" && oldAdder != newAdder) {
    warnings.add("Firm Adder Protection: New Adder was changed")
}

warnings.addAll(api.local.conversionAlerts as List)
warnings.addAll(api.local.manualOverrideWarnings as List)

if(warnings){
    String warningMsg = warnings.join("; ")
    api.yellowAlert(warningMsg)
    return warningMsg
}

return null