if (api.isInputGenerationExecution()) return

String configuratorName = libs.QuoteConstantsLibrary.HeaderConfigurator.INPUTS_NAME
String divisionInputName = libs.QuoteConstantsLibrary.HeaderConfigurator.DIVISION_ID

def uniqueNames = ["P-1431", "P-1433", "P-1420", "P-1425", "P-1416", "P-1422", "P-1421", "P-1419"]

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.in("quoteType", "New Contract", "NewContract"),
        Filter.in("uniqueName", uniqueNames)
]

def newQuotes = api.stream("Q", "lastUpdateDate", *filters)?.withCloseable {
    it.collect {
        [
                uniqueName: it.uniqueName,
                division  : getDivision(it.typedId, configuratorName, divisionInputName)
        ]
    }
}

return newQuotes

String getDivision(String quoteTypedId, String configuratorName, String inputName) {
    return api.getCalculableLineItemCollection(quoteTypedId)?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}