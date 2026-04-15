import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return null

final headerConfiguratorConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd")
SimpleDateFormat sdfOutput = new SimpleDateFormat("MM/dd/yyyy")

def quote = api.currentItem()

def effectiveDate
if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
    effectiveDate = quote.get("inputs").find { it.name == "InputsConfigurator" }?.value?.ContractEffectiveDateInput
} else if (quote?.get("quoteType") == "ExistingContractUpdate") {
    def quoteInputConfigurator = getInputByName(quote?.get("inputs"), headerConfiguratorConstants.INPUTS_NAME)
    def contractPOData = quoteInputConfigurator?.get(headerConfiguratorConstants.CONTRACT_PO_ID)?.data?.find()
    effectiveDate = contractPOData?.get("NewContractPricingDate")
} else {
    return
}

def targetDate = sdfInput.parse(effectiveDate)

def from = sdfOutput.format(targetDate)

return from

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}