if (!quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedSoldTo = headerConfigurator?.get(headerConstants.SOLD_TO_ID) ?: []

def fields = ["attribute12"]
def filter = Filter.equal("customerId", selectedSoldTo)

def soldToIndustry = api.find("C", 0, 1, null, fields, filter)?.find()?.attribute12

return soldToIndustry