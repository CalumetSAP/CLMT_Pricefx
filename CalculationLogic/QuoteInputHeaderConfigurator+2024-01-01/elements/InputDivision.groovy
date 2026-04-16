final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def allDivisions = api.local.division ? api.local.division as Map : [:]
def selectedDivision = out.FindDivision ? out.FindDivision as List : []
def options = allDivisions?.findAll { key, value -> selectedDivision.contains(key) }
def readOnly = !(out.InputSoldTo?.getFirstInput()?.getValue() && api.local.isNotFreightGroup)

api.local.inputDivisionHasChange = false
def callback = { value -> api.local.inputDivisionHasChange = true }

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.DIVISION_ID,
        headerConstants.DIVISION_LABEL,
        true,
        readOnly,
        null,
        options
)

libs.BdpLib.UserInputs.addCallbackConfigurationOnChange(
        entry,
        callback
)

if (api.local.inputSoldToHasChange) {
    entry.getFirstInput().setValue(null)
}

if (options.size() == 1) {
    entry.getFirstInput().setValue(options?.keySet()?.find())
}

return entry