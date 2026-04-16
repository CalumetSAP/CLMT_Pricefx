import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

if (!api.global.isFullListRecalc) return null

String pricelistId = api.global.pricelistId

if (api.global.isFirstRow && pricelistId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    def jobberDealerItems = []
    def srpItems = []
    def mapItems = []
    def start = 0
    def max = api.getMaxFindResultsLimit()
    def someItems, material, priceListNumber, manualOverrides, newListPriceZLIS
    while (someItems = api.namedEntities(api.find("XPLI", start, max,null, Filter.equal("pricelistId", pricelistId)))) {
        for (item in someItems) {
            material = item.sku
            priceListNumber = item["Pricelist Number"]
            manualOverrides = api.jsonDecode(item.manualOverrides as String)
            if (manualOverrides.any { key, value -> value.elementName == "NewJobberDealerPrice" || value.elementName == "JobberDealerPercent"}) {
                jobberDealerItems.add(buildOverriddenItem([material, priceListNumber], item["Jobber / Dealer %"], item.JobberDealerModifiedDatetime, formatter))
            }
            if (manualOverrides.any { key, value -> value.elementName == "NewSRP" || value.elementName == "SRPPercent"}) {
                srpItems.add(buildOverriddenItem([material, priceListNumber], item["SRP %"], item.SRPModifiedDatetime, formatter))
            }
            if (manualOverrides.any { key, value -> value.elementName == "NewMapPrice" || value.elementName == "MAPPercent"}) {
                mapItems.add(buildOverriddenItem([material, priceListNumber], item["MAP %"], item.MAPModifiedDatetime, formatter))
            }
        }
        start += max
    }

    api.global.jobberDealerItems = jobberDealerItems.groupBy { it.key }
    api.global.srpItems = srpItems.groupBy { it.key }
    api.global.mapItems = mapItems.groupBy { it.key }
}

def material = api.local.material
def priceListNumber = api.local.pricelist

api.local.jobberDealerOverride = api.global.jobberDealerItems?.get([material, priceListNumber])?.sort { it.modifiedDateTime }?.last()
api.local.srpOverride = api.global.srpItems?.get([material, priceListNumber])?.sort { it.modifiedDateTime }?.last()
api.local.mapOverride = api.global.mapItems?.get([material, priceListNumber])?.sort { it.modifiedDateTime }?.last()

return null

def buildOverriddenItem (key, value, dateTime, formatter) {
    return [
            key             : key,
            overriddenValue : value,
            modifiedDateTime: dateTime ? LocalDateTime.parse(dateTime as String, formatter) : null,
    ]
}