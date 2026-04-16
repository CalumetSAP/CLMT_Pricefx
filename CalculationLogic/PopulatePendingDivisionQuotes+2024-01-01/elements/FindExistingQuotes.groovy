if (api.isInputGenerationExecution()) return

String configuratorName = libs.QuoteConstantsLibrary.LineItem.DATA_SOURCE_VALUES_HIDDEN_ID
String divisionInputName = "Division"

def uniqueNames = ["P-1486", "P-1487", "P-1489", "P-1490", "P-1491", "P-1492", "P-1493", "P-1494"]

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.equal("quoteType", "ExistingContractUpdate"),
        Filter.in("uniqueName", uniqueNames)
]

def existingQuotes = api.stream("Q", "lastUpdateDate", *filters)?.withCloseable {
    it.collect {
        [
                uniqueName: it.uniqueName,
                division  : getDivision(it.typedId, configuratorName, divisionInputName)
        ]
    }
}

return existingQuotes

String getDivision(String quoteTypedId, String configuratorName, String inputName) {
    /*return api.getCalculableLineItemCollection(quoteTypedId)?.lineItems?.find()?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName) */
    def quoteId = quoteTypedId?.split("\\.")?.getAt(0)?.toLong()

    def qapi = api.queryApi()
    def qli = qapi.tables().quoteLineItems()

    def firstLineItemInputs = qapi.source(qli, [qli.lineId(), qli.inputs()])
            .filter { input -> input.headerId.equal(quoteId) }
            .stream { it.toList() }
            ?.getAt(0)
            ?.inputs

    return firstLineItemInputs
            ?.find { inp -> inp.name == configuratorName }
            ?.value
            ?.get(inputName)
}