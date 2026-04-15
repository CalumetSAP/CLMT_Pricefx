if (api.isInputGenerationExecution() || !InputPricelist?.input?.getValue() || (InputPriceType.input?.getValue() != "3" && InputPriceType.input?.getValue() != "2")) return

def sku = InputMaterial?.input?.getValue()
def pricelist = InputPricelist?.input?.getValue()?.split(" - ")?.getAt(0)

List fields = ["key2", "key3", "key4", "key5", "validFrom", "validTo", "unitOfMeasure", "priceUnit",
               "conditionValue", "currency", "attribute2", "attribute3", "attribute4", "attribute5", "lastUpdateDate"]

List filters = [
        Filter.equal("conditionRecordSetId", out.LoadConditionRecordSetMap["A932"]),
        Filter.equal("key1", "ZBPL"),
        Filter.equal("key5", sku),
        Filter.equal("key2", api.local.salesOrg),
        Filter.equal("key4", pricelist),
]

return api.stream("CRCI5", "-lastUpdateDate", fields, *filters)?.withCloseable {
    it.collect()
}