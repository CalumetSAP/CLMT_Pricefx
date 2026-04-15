import net.pricefx.common.api.InputType

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def selectedSoldTo = InputSoldTo.input?.getValue()
def value

if (selectedSoldTo instanceof List) {
    value = selectedSoldTo
} else {
    if (selectedSoldTo?.customerFieldValue != null) {
        value = [selectedSoldTo?.customerFieldValue]
    } else if (selectedSoldTo?.customerFilterCriteria != null) {
        def customerFilter = api.filterFromMap(selectedSoldTo?.customerFilterCriteria)
        value = api.find("C", 0, api.getMaxFindResultsLimit(), "customerId", ["customerId"], true, customerFilter)?.collect { it.customerId }
    } else {
        value = []
    }
}

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, headerConstants.SOLD_TO_VALUES_HIDDEN_ID)
def input = entry.getFirstInput()
input.setValue(value)

return entry