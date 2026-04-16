def pricelists = out.FindPricelists

def entry = libs.BdpLib.UserInputs.createInputOption(
        libs.DashboardConstantsLibrary.PLTDashboard.PRICELIST_INPUT_KEY,
        libs.DashboardConstantsLibrary.PLTDashboard.PRICELIST_INPUT_LABEL,
        true,
        false,
        null,
        pricelists
)

return entry