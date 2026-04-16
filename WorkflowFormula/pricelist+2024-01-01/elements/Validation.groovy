if (pricelist.headerTypeUniqueName == libs.PricelistLib.Constants.PRICE_LIST_ZBPL_PL_TYPE) {
    Set<String> overriddenMaterials = new HashSet()
    Map<String, Integer> materialsCount = [:]
    def start = 0
    def max = api.getMaxFindResultsLimit()
    def someItems, material, materialCount
    while (someItems = api.find("XPLI", start, max,null, ["sku", "manualOverrides"], Filter.equal("pricelistId", pricelist.id))) {
        for (item in someItems) {
            material = item.sku

            //Get materials with NewListPrice overridden
            if (api.jsonDecode(item.manualOverrides as String)?.any {
                key, value -> ["NewJobberDealerPrice", "JobberDealerPercent", "NewSRP", "SRPPercent", "NewMapPrice", "MAPPercent"].contains(value.elementName)
            }) {
                overriddenMaterials.add(material)
            }

            materialCount = materialsCount[material]
            if (materialCount) {
                materialsCount[material] = materialCount+1
            } else {
                materialsCount[material] = 1
            }
        }
        start += max
    }

    List<String> missingRecalculateMaterials = []
    for (overriddenMaterial in overriddenMaterials) {
        if (materialsCount[overriddenMaterial] > 1) {
            missingRecalculateMaterials.add(overriddenMaterial)
        }
    }
    if (missingRecalculateMaterials) {
        api.throwException("You need to calculate the Price List. There are columns with overridden values that needs to be recalculated. Materials: ${missingRecalculateMaterials.join(", ")}")
    }
}