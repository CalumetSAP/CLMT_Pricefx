if (!quoteProcessor.isPostPhase()) return

String commandExecution = api.currentContext().commandName
if (!["calculate", "submit", "testexec", "creationworkflowsubmit"].contains(commandExecution)) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def alerts = []

def alertsAux
def thirdParty, namedPlace, customerMaterial
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    alertsAux = []
    thirdParty = getInputByName(lnProduct?.inputs, lineItemConstants.THIRD_PARTY_CUSTOMER_ID)
    namedPlace = getInputByName(lnProduct?.inputs, lineItemConstants.NAMED_PLACE_ID)
    customerMaterial = getInputByName(lnProduct?.inputs, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)
    if (thirdParty?.size() > 35) alertsAux.add("3rd Party Customer Support is too long (max. 35)")
    if (namedPlace?.size() > 28) alertsAux.add("Named Place is too long (max. 28)")
    if (customerMaterial?.size() > 35) alertsAux.add("Customer Material # is too long (max. 35)")
    if (alertsAux) alerts.add(lnProduct?.sku + " - " + alertsAux.join(", "))
}

if (alerts) api.yellowAlert(alerts.join(", "))

if (commandExecution == "submit") {
    def approverLevel, formulaApprover, approver, needApproval
    def approverAlerts = []
    def errorList = ["Invalid Approver in Approvers Table", "No Approver"]
    for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
        if (lnProduct.folder) continue
        needApproval = getInputByName(lnProduct?.inputs, lineItemConstants.LINE_HAS_CHANGED_ID)
        if (!needApproval) continue
        // Formula Approver
        formulaApprover = getInputByName(lnProduct?.inputs, lineItemConstants.FORMULA_APPROVER_HIDDEN_ID)
        if (errorList.contains(formulaApprover))
            approverAlerts.add(getErrorMessage(formulaApprover, "2", lnProduct.sku))

        // Discount Approver
        approverLevel = getInputByName(lnProduct?.inputs, lineItemConstants.APPROVER_LEVEL_HIDDEN_ID)
        if (!approverLevel) continue

        approver = getInputByName(lnProduct?.inputs, lineItemConstants.APPROVER_HIDDEN_ID)
        if (errorList.contains(approver)) {
            approverAlerts.add(getErrorMessage(approver, approverLevel, lnProduct.sku))
        }
    }

    if (approverAlerts) api.throwException(approverAlerts.join(", "))
}

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getErrorMessage(value, level, sku) {
    if (value == "Invalid Approver in Approvers Table") {
        return  "Invalid Approver level " + level + " in Approvers Table CPT for material " + sku
    } else if (value == "No Approver") {
        return "No level " + level + " approver defined for material " + sku
    } else {
        return null
    }
}