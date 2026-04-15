if (api.isInputGenerationExecution()) return

String configuratorName = libs.QuoteConstantsLibrary.LineItem.NEW_QUOTE_CONFIGURATOR_NAME
String salesPersonInputName = libs.QuoteConstantsLibrary.LineItem.SALES_PERSON_ID
Date today = new Date()

def filters = [
        Filter.lessThan("expiryDate", today),
        Filter.equal("quoteStatus", "OFFER"),
        Filter.or(
                Filter.isNull("attributeExtension___Expired"),
                Filter.notEqual("attributeExtension___Expired", "Yes")
        )
//        Filter.in("uniqueName", ["P-851", "P-843", "P-848"]) //For testing
]

def quotesToExpire = api.stream("Q", null, *filters)?.withCloseable {
    it?.collect { [
            uniqueName      : it.uniqueName,
            label           : it.label,
            salesPersonsIds : getSalesPersonsCustomerIds(it.typedId, configuratorName, salesPersonInputName)
    ] }
}

return quotesToExpire

HashSet<String> getSalesPersonsCustomerIds (String quoteTypedId, String configuratorName, String inputName) {
    HashSet<String> customerIds = new HashSet<>()

    api.getCalculableLineItemCollection(quoteTypedId)?.lineItems?.inputs?.
            collect { liInputs ->
                liInputs.find { input -> input.name == configuratorName}?.value
            }?.
            getAt(inputName)?.
            each { salesPerson -> customerIds.add(salesPerson?.split(" - ")?.getAt(0)?.trim()) }
    customerIds.remove(null)

    return customerIds
}