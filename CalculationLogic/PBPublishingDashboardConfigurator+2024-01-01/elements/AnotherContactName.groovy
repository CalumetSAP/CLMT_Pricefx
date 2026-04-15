
String anotherContactName = "Another Contact Name"

def entry = libs.BdpLib.UserInputs.createInputString(
        libs.DashboardConstantsLibrary.PricePublishing.ANOTHER_CONTACT_NAME_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.ANOTHER_CONTACT_NAME_LABEL,
        false,
        false,
        anotherContactName,false
)


    entry.getFirstInput().setValue(anotherContactName)


return entry