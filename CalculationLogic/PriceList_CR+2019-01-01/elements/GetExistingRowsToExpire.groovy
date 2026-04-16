import java.text.SimpleDateFormat

if (!api.local.isPricelistZBPL) {
    api.local.existingRowsToExpire = []
    api.local.delayedItemsFromZBPL = []
    return
}

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def conditionRecordSetMap = out.LoadConditionRecordSetMap

def newItems = api.local.newItems as List
def zbplRows = getZBPLRows(newItems)
def existingCRs = getExtistingCRs(newItems, conditionRecordSetMap["A932"])
def zbplRowsToExpire = []
def newItemsToDelay = []

def key, zbplRow, existingCR, itemToAdd, newItemValidFrom, zbplValidFrom
newItems?.each { newItem ->
    key = newItem.key5 + "|" + newItem.key2 + "|" + newItem.key4
    zbplRow = zbplRows?.get(key)
    existingCR = existingCRs?.get([newItem.key5, newItem.key2, newItem.key4])
    if (!zbplRow || zbplHasCR(zbplRow, existingCR)) return

    def outputFormat = new SimpleDateFormat("yyyy-MM-dd")

    itemToAdd = [:]
//    itemToAdd.conditionRecordSetName = "A932"
    itemToAdd.conditionRecordSetId = conditionRecordSetMap["A932"]
    itemToAdd.key1 = "ZBPL"
    itemToAdd.key2 = newItem.key2
    itemToAdd.key3 = zbplRow.DistributionChannel
    itemToAdd.key4 = newItem.key4
    itemToAdd.key5 = newItem.key5
    itemToAdd.unitOfMeasure = zbplRow.UnitOfMeasure
    itemToAdd.priceUnit = zbplRow.Per
    itemToAdd.conditionValue = zbplRow.Amount
    itemToAdd.currency = zbplRow.ConditionCurrency
    itemToAdd.integrationStatus = 0
    itemToAdd.attribute1 = newItem.attribute1

    newItemValidFrom = sdf.parse(newItem.validFrom)
    zbplValidFrom = zbplRow.ValidFrom
    itemToAdd.validFrom = outputFormat.format(zbplValidFrom)

    if (newItemValidFrom <= zbplValidFrom) { //Scenario 1
        itemToAdd.validTo = outputFormat.format(zbplRow.ValidTo)
        itemToAdd.attribute4 = "Delete"
        newItemsToDelay.add(newItem)

    } else if (zbplValidFrom < newItemValidFrom) { //Scenario 2
        Calendar cal = Calendar.getInstance()
        cal.setTime(newItemValidFrom)
        cal.add(Calendar.DAY_OF_MONTH, -1)

        itemToAdd.validTo = outputFormat.format(cal.getTime())
        itemToAdd.attribute4 = "Change"
    }

    zbplRowsToExpire.add(itemToAdd)
}

api.local.newItems.removeAll(newItemsToDelay)
api.local.delayedItemsFromZBPL = newItemsToDelay
api.local.existingRowsToExpire = zbplRowsToExpire

return null

def getZBPLRows(List items) {
    def ctx = api.getDatamartContext()
    def dm = ctx.getDataSource("ZBPL")

    def customFilter = Filter.and(
            Filter.in("Material", items?.collect { it.key5 }?.unique()),
            Filter.in("Pricelist", items?.collect { it.key4 }?.unique()),
            Filter.in("SalesOrganization", items?.collect { it.key2 }?.unique())
    )

    def query = ctx.newQuery(dm, false)
            .select("Material", "Material")
            .select("SalesOrganization", "SalesOrganization")
            .select("Pricelist", "Pricelist")
            .select("ValidFrom", "ValidFrom")
            .select("ValidTo", "ValidTo")
            .select("Amount", "Amount")
            .select("DistributionChannel", "DistributionChannel")
            .select("UnitOfMeasure", "UnitOfMeasure")
            .select("Per", "Per")
            .select("ConditionCurrency", "ConditionCurrency")
            .select("lastUpdateDate", "lastUpdateDate")
            .where(customFilter)
            .orderBy("lastUpdateDate DESC")

    def data = ctx.executeQuery(query)?.getData()
    def map = [:]
    def key
    data?.each {
        key = it.Material + "|" + it.SalesOrganization + "|" + it.Pricelist
        map.putIfAbsent(key, it)
    }

    return map
}

def getExtistingCRs(List items, conditionRecordSetId) {
    def keys2 = items?.collect { it.key2 }?.unique()
    def keys3 = items?.collect { it.key3 }?.unique()
    def keys4 = items?.collect { it.key4 }?.unique()
    def keys5 = items?.collect { it.key5 }?.unique()

    List filters = [
            Filter.equal("conditionRecordSetId", conditionRecordSetId),
            Filter.equal("key1", "ZBPL"),
            Filter.in("attribute4", ["Change", "Delete"]),
            Filter.isNull("attribute5")
    ]

    if (keys2) filters.add(Filter.in("key2", keys2))
    if (keys3) filters.add(Filter.in("key3", keys3))
    if (keys4) filters.add(Filter.in("key4", keys4))
    if (keys5) filters.add(Filter.in("key5", keys5))

    return api.stream("CRCI5", null, *filters)?.withCloseable { stream ->
        stream.collect()?.groupBy {[it.key5, it.key2, it.key4] }
    } ?: [:]
}

def zbplHasCR(zbplRow, List existingCR) {
    if (!existingCR) return false

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    def linesWithSameValidFrom = existingCR?.findAll {
        sdf.parse(it.validFrom as String) == zbplRow.ValidFrom
    }
    def maxLastUpdateRow = linesWithSameValidFrom?.max { it.lastUpdateDate }
    if (!maxLastUpdateRow) return false

    return dateTimeFormat.parse(maxLastUpdateRow.lastUpdateDate) >= zbplRow.lastUpdateDate?.toDate()
}