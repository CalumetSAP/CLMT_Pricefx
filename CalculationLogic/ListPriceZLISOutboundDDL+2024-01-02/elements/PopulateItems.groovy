if (api.isInputGenerationExecution()) return

final roundingUtils = libs.SharedLib.RoundingUtils

def plId = api.isDebugMode() ? "587" : dist?.calcItem?.Key2

loader = api.isDebugMode() ? [] : dist?.dataLoader

Map plItems = libs.PricelistLib.Common.getAllPLItems(plId)
        ?.findAll { it["New Effective Date"] && it["New List Price (ZLIS)"] }
        ?.groupBy { [it["sku"], it["Sales Org"], it["New Effective Date"]] }
        ?.collectEntries { [(it.key): it.value.find()] } ?: [:]

//api.trace("plItems", null, plItems)

Set<String> productIds = new HashSet<>()
Set<String> effectiveDates = new HashSet<>()

for (key in plItems.keySet()) {
    productIds.add(key[0])
    effectiveDates.add(key[2])
}

def plItemValue, newItem
for (plItem in plItems) {

    plItemValue = plItem.value

//    api.trace("plItemValue", null, plItemValue)

    if (!plItemValue) continue

    newItem = [
            Material: plItemValue["sku"],
            A_Table_Number: "A901",
    ]
    loader.addRow(updateCommonFields(newItem, plItemValue, plId, roundingUtils))
}

api.trace("loader", null, loader)

Map updateCommonFields (itemToUpdate, plItem, String plId, roundingUtils) {
    itemToUpdate.Sales_Org = plItem["Sales Org"]
    itemToUpdate.Valid_From = plItem["New Effective Date"]
    itemToUpdate.Valid_To = plItem["New Expiration Date"]
    itemToUpdate.UOM = plItem["ZLIS UOM"]
    itemToUpdate.Per = plItem["Per"]
    itemToUpdate.Amount = plItem["New List Price (ZLIS)"]
    itemToUpdate.Currency = plItem["Currency"]
    itemToUpdate.ConditionRecordNo = plId + "-" + plItem["typedId"].tokenize(".")[0]

    return itemToUpdate
}