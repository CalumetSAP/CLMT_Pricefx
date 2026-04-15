final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnly = !api.local.isNotFreightGroup
def today = libs.QuoteLibrary.DateUtils.getToday()

def entry = libs.BdpLib.UserInputs.createInputDate(
        headerConstants.CONTRACT_EFFECTIVE_DATE_ID,
        headerConstants.CONTRACT_EFFECTIVE_DATE_LABEL,
        true,
        readOnly,
        today
)

entry.getFirstInput().setConfigParameter("noRefresh", true)

return entry
