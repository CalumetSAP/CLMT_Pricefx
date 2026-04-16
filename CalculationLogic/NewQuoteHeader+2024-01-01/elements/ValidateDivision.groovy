if (api.isInputGenerationExecution()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)

def productMasterData = out.FindProductMasterData ?: [:]
def divisionError = []

productMasterData?.each { key, value ->
    if (division != value.Division) divisionError.add(key)
}

api.local.divisionError = divisionError

return null