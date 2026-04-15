def shipTos = api.local.quoteItems?.collect { getOutputByName(it.outputs, "ShipTo") }?.unique()
if (!shipTos?.contains(null)) return null //If there are no "ship to" null values, means that the third scenario will never happen so no query to PartnerKNVP needed

final headerConfiguratorConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemInputsConstants = libs.QuoteConstantsLibrary.LineItem

def quoteInputConfigurator = getInputByName(api.local.quote?.inputs, headerConfiguratorConstants.INPUTS_NAME)

def soldTos = quoteInputConfigurator?.get(headerConfiguratorConstants.SOLD_TO_ID) ?: []
soldTos.remove(null)

def divisions = api.local.quoteItems?.collect { getInputByName(it?.inputs, lineItemInputsConstants.DATA_SOURCE_VALUES_HIDDEN_ID)?.get("Division") }?.unique()
divisions.remove(null)

def distributionChannels = [:]
if (soldTos && divisions) {
    def filters = [
            Filter.equal("name", "PartnerKNVP"),
            Filter.in("attribute1", soldTos),
            Filter.in("attribute3", divisions),
    ]

    distributionChannels = api.stream("CX20", null, ["attribute1", "attribute3", "attribute5"], true, *filters).withCloseable {
        it.collectEntries {
            [([it.attribute1, it.attribute3]): it.attribute5]
        }
    } ?: [:]
}

api.local.distributionChannels = distributionChannels

return null

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}