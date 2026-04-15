import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

def workflowData = libs.QuoteLibrary.Calculations.getApprovedAndSubmissionDate(quote?.uniqueName)
def approvalDate = workflowData.ApprovalDate ?: workflowData.SubmissionDate

if (!approvalDate) approvalDate  = new Date().format("yyyy-MM-dd")

SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd")
Date date = inputDateFormat.parse(approvalDate)

def defaultExpiryDate = sumDays(date, 30)

api.update("Q", [
        "uniqueName": quote?.uniqueName,
        "targetDate": date,
        "expiryDate": defaultExpiryDate
])

return null

def sumDays(Date date, days) {
    if (!date || !days) return date
    def newDate = Calendar.getInstance()
    newDate.setTime(date)
    newDate.add(Calendar.DAY_OF_MONTH, days)
    return newDate.getTime()
}