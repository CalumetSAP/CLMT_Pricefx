import java.text.SimpleDateFormat

def today = new Date()
def expireDate = new SimpleDateFormat("yyyy-MM-dd").parse(quote?.expiryDate)
if (today > expireDate && "offer".equalsIgnoreCase(quote.quoteStatus)) {
//    api.logInfo("result", "Quote with customer CD-00008 is not allowed to convert to Deal.")
    api.setAlertMessage("Quote not allow to convert to Deal.")
//    api.sendEmail()
    return false
} else {
    //api.logInfo("result", "Quote can be converted to Deal.")
    return true
}

