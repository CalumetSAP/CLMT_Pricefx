import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def today = new Date()
def entry = libs.BdpLib.UserInputs.createInputDate(
        "CalculationDateInput",
        "Calculation Date",
        true,
        false,
        sdf.format(today)
)

return entry
