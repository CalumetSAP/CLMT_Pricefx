if (api.isInputGenerationExecution()) return

final roundingUtils = libs.SharedLib.RoundingUtils

def plId = api.isDebugMode() ? "499" : dist?.calcItem?.Key2

loader = api.isDebugMode() ? [] : dist?.dataLoader

Map plItems = libs.PricelistLib.Common.getAllPLItems(plId)
        ?.findAll { it["Pricelist Number"] && it["New Effective Date"] }
        ?.groupBy { [it["sku"], it["Pricelist Number"], it["New Effective Date"]] }
        ?.collectEntries { [(it.key): it.value.find()] } ?: [:]
//api.trace("plItems", plItems)

Set<String> productIds = new HashSet<>()
Set<String> priceListIds = new HashSet<>()
Set<String> effectiveDates = new HashSet<>()
for (key in plItems.keySet()) {
    productIds.add(key[0])
    priceListIds.add(key[1])
    effectiveDates.add(key[2])
}

Map basePricingItems = getBasePricingDSItems(productIds, priceListIds, effectiveDates)
//api.trace("basePricingItems", basePricingItems)

def basePricingItem, plItemValue, newItem
for (plItem in plItems) {
    plItemValue = plItem.value
    if (!plItemValue) continue
    basePricingItem = basePricingItems[plItem.key]
    if (basePricingItem) {
        loader.addRow(updateCommonFields(basePricingItem.find(), plItemValue, plId, roundingUtils))
    } else {
        newItem = [
                ProductId       : plItemValue["sku"],
                PricelistID     : plItemValue["Pricelist Number"],
                EffectiveDate   : plItemValue["New Effective Date"],
        ]
        loader.addRow(updateCommonFields(newItem, plItemValue, plId, roundingUtils))
    }
}
//api.trace("loader", loader)

Map getBasePricingDSItems (Set productIds, Set priceListIds, Set effectiveDates) {
    List filters = [
            Filter.in("ProductId", productIds),
            Filter.in("PricelistID", priceListIds),
            Filter.in("EffectiveDate", effectiveDates)
    ]

    def ctx = api.getDatamartContext()
    def ds = ctx.getDataSource("BasePricing")

    def query = ctx.newQuery(ds, false)
    query = query
            .selectAll(true)
            .setUseCache(false)
            .where(*filters)
            .orderBy("lastUpdateDate DESC")

    return ctx.executeQuery(query)?.getData()?.groupBy { [it.ProductId, it.PricelistID, it.EffectiveDate?.toString()] } ?: [:]
}

Map updateCommonFields (itemToUpdate, plItem, String plId, roundingUtils) {
    itemToUpdate.ExpirationDate = plItem["New Expiration Date"]
    itemToUpdate.Cost = roundingUtils.round(plItem["Cost"], 3)
    itemToUpdate.UOM = plItem["ZBPL UOM"] ?: plItem["ZLIS UOM"]
    itemToUpdate.SalesOrg = plItem["Sales Org"]
    itemToUpdate.JobberPrice = roundingUtils.round(plItem["New Jobber / Dealer Price (Each)"], 3)
    itemToUpdate.SRP = roundingUtils.round(plItem["New SRP (Each)"], 3)
    itemToUpdate.MAP = roundingUtils.round(plItem["New MAP Price (Each)"], 3)
    itemToUpdate.JobberPercent = roundingUtils.round(plItem["Jobber / Dealer %"], 2)
    itemToUpdate.SRPPercent = roundingUtils.round(plItem["SRP %"], 2)
    itemToUpdate.MAPPercent = roundingUtils.round(plItem["MAP %"], 2)
    itemToUpdate.PricefxID = plId

    return itemToUpdate
}