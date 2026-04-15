def today = new Date().format("yyyy-MM-dd")

def entry = libs.BdpLib.UserInputs.createInputDate(
        libs.DashboardConstantsLibrary.PLTDashboard.PRICING_DATE_INPUT_KEY,
        libs.DashboardConstantsLibrary.PLTDashboard.PRICING_DATE_INPUT_LABEL,
        false,
        false,
        today
)
return entry