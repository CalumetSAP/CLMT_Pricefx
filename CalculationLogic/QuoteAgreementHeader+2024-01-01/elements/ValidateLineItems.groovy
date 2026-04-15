if (!quoteProcessor.isPostPhase()) return

String commandExecution = api.currentContext().commandName
if (!["calculate", "submit", "testexec", "creationworkflowsubmit"].contains(commandExecution)) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = customerConfigurator?.get(headerConstants.DIVISION_ID)

def productMasterData = out.FindProducts ?: [:]
def divisionError = []

productMasterData?.each { key, value ->
    if (division != value.Division) divisionError.add(key)
}

if (divisionError) api.yellowAlert("The following Materials have a different division: " + divisionError.join(", "))

return