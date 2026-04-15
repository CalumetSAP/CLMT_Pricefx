import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

Date parsedEffDate

parsedEffDate = sdf.parse("9999-12-31")


Calendar effectiveDate = Calendar.getInstance()
effectiveDate.setTime(parsedEffDate)

def entry = libs.BdpLib.UserInputs.createInputDate(
        "ExpirationDateInput",
        "Expiration Date",
        true,
        false,
        effectiveDate
)

return entry