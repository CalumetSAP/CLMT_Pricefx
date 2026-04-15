if (api.isInputGenerationExecution()) return

String configuratorName = libs.QuoteConstantsLibrary.HeaderConfigurator.INPUTS_NAME
String divisionInputName = libs.QuoteConstantsLibrary.HeaderConfigurator.DIVISION_ID

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.in("quoteType", ["NewContract", "New Contract"])
]

def newQuotes = api.find("Q", 0, 200, null, *filters)?.collect {
    [
            uniqueName: it.uniqueName,
            division  : getDivision(it.typedId, configuratorName, divisionInputName)
    ]
}

return newQuotes

String getDivision(String quoteTypedId, String configuratorName, String inputName) {
    return api.getCalculableLineItemCollection(quoteTypedId)?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}