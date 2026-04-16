if (api.isInputGenerationExecution()) return

def configurators = quote?.lineItems?.inputs?.flatten()?.findAll { it.name == "Inputs"}?.value

def priceType = configurators?.PriceTypeInput?.find { it == "1"}
def formulaApprovers = quote?.lineItems?.inputs?.flatten()?.findAll { it.name == "FormulaApproverHiddenInput" && it.value}?.collect { it.value }?.flatten()?.unique()

def approversMap = [
        "1": [],
        "2": [],
        "3": []
]
def approverLevel, formulaApprover, approver
def alerts = []
def errorList = ["Invalid Approver in Approvers Table", "No Approver"]

quote?.lineItems?.each { lineItem ->
    // Formula Approver
    formulaApprover = lineItem?.inputs?.find { it.name == "FormulaApproverHiddenInput"}?.value
    if (errorList.contains(formulaApprover))
        alerts.add(getErrorMessage(formulaApprover, "2", lineItem.sku))

    // Discount Approver
    approverLevel = lineItem?.inputs?.find { it.name == "ApproverLevelHiddenInput"}?.value?.toString()
    if (!approverLevel) return

    approver = lineItem?.inputs?.find { it.name == "ApproverHiddenInput"}?.value
    if (errorList.contains(formulaApprover)) {
        alerts.add(getErrorMessage(approver, approverLevel, lineItem.sku))
    } else {
        approversMap?.get(approverLevel)?.add(approver)
    }
}

if (alerts) api.throwException(alerts.join(", "))

api.local.hasPricingFormula = priceType ? true : false
api.local.formulaApprovers = formulaApprovers ?: []
api.local.approversMap = approversMap

def getErrorMessage(value, level, sku) {
    if (value == "Invalid Approver in Approvers Table") {
        return  "Invalid Approver level " + level + " in Approvers Table CPT for material " + sku
    } else if (value == "No Approver") {
        return "No level " + level + " approver defined for material " + sku
    } else {
        return null
    }
}