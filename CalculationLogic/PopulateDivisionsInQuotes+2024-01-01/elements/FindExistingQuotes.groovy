if (api.isInputGenerationExecution()) return

String configuratorName = libs.QuoteConstantsLibrary.LineItem.DATA_SOURCE_VALUES_HIDDEN_ID
String divisionInputName = "Division"

def filters = [
        Filter.or(
                Filter.isNull("attributeExtension___Division")
        ),
        Filter.equal("quoteType", "ExistingContractUpdate")
]

def existingQuotes = api.find("Q", 0, 200, null, *filters)?.collect {
    [
            uniqueName: it.uniqueName,
            division  : getDivision(it.typedId, configuratorName, divisionInputName)
    ]
}

return existingQuotes

String getDivision(String quoteTypedId, String configuratorName, String inputName) {
    return api.getCalculableLineItemCollection(quoteTypedId)?.lineItems?.find()?.inputs?.
            find { inp -> inp.name == configuratorName
            }?.value?.get(inputName)
}