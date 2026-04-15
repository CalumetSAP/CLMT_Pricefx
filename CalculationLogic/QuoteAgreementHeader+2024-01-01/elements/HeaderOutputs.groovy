if (!quoteProcessor.isPostPhase()) return

final createOutput = libs.BdpLib.QuoteOutput
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final headerOutputsConstants = libs.QuoteConstantsLibrary.HeaderOutputs

def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]


def columns = ["SAP Contract Number", "Ship-to", "Sold-to"]
def contractNumbersMatrix = api.newMatrix(columns)

def industryList = out.FindIndustryList ?: []

quoteProcessor.with {
    quoteProcessor.addOrUpdateOutput("ROOT", [
            "resultName" : headerOutputsConstants.SAP_CONTRACT_NUMBERS_ID,
            "resultLabel": headerOutputsConstants.SAP_CONTRACT_NUMBERS_LABEL,
            "result"     : contractNumbersMatrix,
            "resultType" : "MATRIX",
            "resultGroup": "",
    ])

    quoteProcessor.addOrUpdateOutput("ROOT", createOutput.text(
            headerOutputsConstants.INDUSTRY_ID,
            headerOutputsConstants.INDUSTRY_LABEL,
            null,
            industryList?.join(", ")
    ))
}

return null
