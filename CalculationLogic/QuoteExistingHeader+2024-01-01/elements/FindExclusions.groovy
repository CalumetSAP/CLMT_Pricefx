if (!quoteProcessor.isPostPhase() || !api.local.lineItemSkus) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def selectedSoldTo = getSoldToValues(headerConfigurator?.get(headerConstants.SOLD_TO_ID))

def soldTos = selectedSoldTo + ["*"]
soldTos.remove(null)

def filters = []
filters.add(Filter.in("key4", api.local.lineItemSkus + ["*"]))
filters.add(Filter.in("key1", out.FindProductMasterData?.values()?.PH1Code + ["*"]))
if (api.local.lineItemShipTos) filters.add(Filter.in("key3", api.local.lineItemShipTos + ["*"]))

return libs.PricelistLib.Common.getExclusions(soldTos, Filter.and(*filters))

def getSoldToValues(selectedSoldTo) {
    if (selectedSoldTo instanceof List) return selectedSoldTo

    if (selectedSoldTo?.customerFieldValue != null) {
        return [selectedSoldTo?.customerFieldValue]
    } else if (selectedSoldTo?.customerFilterCriteria != null) {
        def customerFilter = api.filterFromMap(selectedSoldTo?.customerFilterCriteria)
        return api.find("C", 0, api.getMaxFindResultsLimit(), "customerId", ["customerId"], true, customerFilter)?.collect { it.customerId }
    } else {
        return []
    }
}