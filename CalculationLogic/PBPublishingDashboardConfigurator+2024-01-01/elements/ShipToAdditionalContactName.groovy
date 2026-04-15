
def entry = libs.BdpLib.UserInputs.createInputString(
        libs.DashboardConstantsLibrary.PricePublishing.ANOTHER_CONTACT_NAME_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.ANOTHER_CONTACT_NAME_LABEL,
        false,
        false,
        "Additional Contact Name"
)

entry.getFirstInput().setValue()


return entry