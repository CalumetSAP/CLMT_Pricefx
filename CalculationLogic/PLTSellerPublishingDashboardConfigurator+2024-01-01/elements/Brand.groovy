def brands = out.FindBrands

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PLTDashboard.BRAND_INPUT_KEY,
        libs.DashboardConstantsLibrary.PLTDashboard.BRAND_INPUT_LABEL,
        false,
        false,
        null,
        brands
)

return entry