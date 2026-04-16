import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def effDate = out.EffectiveDate?.getFirstInput()?.getValue()
effDate = sdf.parse(effDate)
Calendar effectiveDate = Calendar.getInstance()
effectiveDate.setTime(effDate)
effectiveDate.add(Calendar.DAY_OF_YEAR, 365)

def entry = libs.BdpLib.UserInputs.createInputDate(
        "ExpirationDateInput",
        "Expiration Date",
        true,
        false,
        effectiveDate
)

return entry