final calculations = libs.QuoteLibrary.Calculations

def errorList = input["errorList"]

if (!errorList) return

errorList.each {
    calculations.noGuardrailOrApproverFound(it.Group, it.Subject, it?.EmailText)
}