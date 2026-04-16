if (input.NextPricing == true) return

def today = new Date().format("yyyy-MM-dd")

def entry = libs.BdpLib.UserInputs.createInputDate(
        libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_LABEL,
        false,
        false,
        today
)
return entry