import java.text.SimpleDateFormat

String pricelistId = api.global.pricelistId

// when the new batch starts, do pre-load products (for all SKUs of the batch) into memory
if (libs.SharedLib.BatchUtils.isNewBatch() && pricelistId) {
    List filters = [
            Filter.equal("pricelistId", pricelistId),
            Filter.isNotEmpty("manualOverrides"),
            Filter.in("sku", api.global.currentBatch),
    ]

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    List<Date> overrideEffectiveDates = []
    String stringDate
    api.stream("XPLI", null, ["manualOverrides"], *filters)
            .withCloseable {
                it.each { item ->
                    stringDate = api.jsonDecode(item.manualOverrides as String)?.find { key, value -> value.elementName == "NewEffectiveDate" }?.value?.value
                    if (stringDate) {
                        overrideEffectiveDates.add(sdf.parse(stringDate))
                    }
                }
            }

    Boolean noEffectiveDatesOverridden = overrideEffectiveDates.isEmpty()
    api.global.noEffectiveDatesOverridden = noEffectiveDatesOverridden

    if (!noEffectiveDatesOverridden) {
        overrideEffectiveDates.add(sdf.parse(api.global.effectiveDate))
        overrideEffectiveDates.add(api.local.newEffectiveDate)

        api.global.minNewEffectiveDate = overrideEffectiveDates.min()
        api.global.maxNewEffectiveDate = overrideEffectiveDates.max()
    }
    return null
}

return null