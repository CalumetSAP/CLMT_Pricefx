if (!quoteProcessor.isPostPhase()) return

String commandExecution = api.currentContext().commandName
if (!["calculate", "submit", "testexec", "creationworkflowsubmit"].contains(commandExecution)) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

if (!customerConfigurator?.get(headerConstants.CONTRACT_NUMBER_ID)) {
    api.criticalAlert("Contract Number input is required")
}

if (api.local.invalidSoldTos) {
    api.yellowAlert("The following Sold Tos are no longer valid: " + api.local.invalidSoldTos)
}

return null