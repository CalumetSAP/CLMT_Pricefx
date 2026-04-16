def newItems = (api.local.newItems ?: []) + (api.local.existingRowsToExpire ?: [])
def supersededItems = api.local.supersededItems ?: []
def itemsToUpdate = api.local.itemsToUpdate ?: []
def delayedItems = (api.local.delayedItems ?: []) + (api.local.delayedItemsFromZBPL ?: [])

def typeCode = api.local.isMassEdit || api.local.isPricingFormula || api.local.isPricelistZBPL ? "CRCI5" : "CRCI3"

newItems = addLastModifiedByAndCreatedBy(newItems)
supersededItems = addLastModifiedByAndCreatedBy(supersededItems)
itemsToUpdate = addLastModifiedByAndCreatedBy(itemsToUpdate)
delayedItems = addLastModifiedByAndCreatedBy(delayedItems)


populateOrUpdate(newItems, supersededItems, itemsToUpdate, typeCode)
addPendingLines(delayedItems)

if(api.isInputGenerationExecution()) return // if saving logic from navigator, prevent addConditionRecordAction throw an exception (error because of a logWarn)
//conditionRecordHelper.addConditionRecordAction().setCalculate(true) //TODO remove in a future? Is deprecated but now it is the only way to make it work

if (api.local.tablesUpdated) {
    for (tableNr in api.local.tablesUpdated) {
        api.customEvent([
                Process : "CondRecordGenerated",
                TableNr : tableNr
        ], "ConditionRecord")
    }
}

return null

def populateOrUpdate (List newItems, supersededItems, List itemsToUpdate, typeCode) {
    for (supersededItem in supersededItems) {
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
        api.addOrUpdate(typeCode, supersededItem)
        //conditionRecordHelper.addOrUpdate(typeCode,supersededItem)
        /*if(supersededItem != null
                && supersededItem.conditionRecordSetId != null
                && supersededItem.conditionValue != null
                && supersededItem.validFrom != null
                && supersededItem.validTo != null
                && supersededItem.attribute5 != null)
        {
            conditionRecordHelper.addOrUpdate([
                    key1: supersededItem.key1,
                    key2: supersededItem.key2,
                    key3: supersededItem.key3,
                    key4: supersededItem.key4,
                    key5: supersededItem.key5,
                    validTo: supersededItem.validTo.format("yyyy-MM-dd"),
                    validFrom: supersededItem.validFrom.format("yyyy-MM-dd"),
                    unitOfMeasure: supersededItem.unitOfMeasure,
                    priceUnit:supersededItem.priceUnit,
                    conditionValue: supersededItem.conditionValue,
                    currency: supersededItem.currency,
                    conditionRecordSetId: supersededItem.conditionRecordSetId,
                    attribute1: supersededItem.attribute1?.value,
                    attribute4: supersededItem.attribute4?.value,
                    attribute5: supersededItem.attribute5.value
            ])
        }
        else if (supersededItem != null
                && supersededItem.conditionRecordSetId != null
                && supersededItem.validFrom != null
                && supersededItem.validTo != null) {

            // Partial record — shorten validTo only, using today as the cutoff
            conditionRecordHelper.shortenValidTo([
                    key1:                 supersededItem.key1,
                    key2:                 supersededItem.key2,
                    key3:                 supersededItem.key3,
                    key4:                 supersededItem.key4,
                    key5:                 supersededItem.key5,
                    validFrom:            supersededItem.validFrom.format("yyyy-MM-dd"),
                    validTo:              new Date().format("yyyy-MM-dd"),  // shorten to today
                    conditionRecordSetId: supersededItem.conditionRecordSetId
            ])

        } else {
            // supersededItem is null or missing even the minimum fields to identify the record
            api.logInfo("CR ShortenValidTo Skipped",
                    "supersededItem is null or missing conditionRecordSetId/validFrom/validTo — nothing to shorten")
        }*/



    }
    for (itemToUpdate in itemsToUpdate) {
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
        api.addOrUpdate(typeCode,itemToUpdate)
       //conditionRecordHelper.addOrUpdate(typeCode,itemToUpdate)
     /*    if(itemToUpdate != null
                && itemToUpdate.conditionRecordSetId != null
                && itemToUpdate.conditionValue != null
                && itemToUpdate.validFrom != null
                && itemToUpdate.validTo != null
                && itemToUpdate.attribute5 != null)
        {
            conditionRecordHelper.addOrUpdate([
                    key1: itemToUpdate.key1,
                    key2: itemToUpdate.key2,
                    key3: itemToUpdate.key3,
                    key4: itemToUpdate.key4,
                    key5: itemToUpdate.key5,
                    validTo: itemToUpdate.validTo.format("yyyy-MM-dd"),
                    validFrom: itemToUpdate.validFrom.format("yyyy-MM-dd"),
                    unitOfMeasure: itemToUpdate.unitOfMeasure,
                    priceUnit:itemToUpdate.priceUnit,
                    conditionValue: itemToUpdate.conditionValue,
                    currency: itemToUpdate.currency,
                    conditionRecordSetId: itemToUpdate.conditionRecordSetId,
                    attribute1: itemToUpdate.attribute1?.value,
                    attribute4: itemToUpdate.attribute4?.value,
                    attribute5: itemToUpdate.attribute5.value
            ])
        }
        else if (itemToUpdate != null
                && itemToUpdate.conditionRecordSetId != null
                && itemToUpdate.validFrom != null
                && itemToUpdate.validTo != null) {

            // Partial record — shorten validTo only, using today as the cutoff
            conditionRecordHelper.shortenValidTo([
                    key1:                 itemToUpdate.key1,
                    key2:                 itemToUpdate.key2,
                    key3:                 itemToUpdate.key3,
                    key4:                 itemToUpdate.key4,
                    key5:                 itemToUpdate.key5,
                    validFrom:            itemToUpdate.validFrom.format("yyyy-MM-dd"),
                    validTo:              new Date().format("yyyy-MM-dd"),  // shorten to today
                    conditionRecordSetId: itemToUpdate.conditionRecordSetId
            ])

        } else {
            // supersededItem is null or missing even the minimum fields to identify the record
            api.logInfo("CR ShortenValidTo Skipped",
                    "supersededItem is null or missing conditionRecordSetId/validFrom/validTo — nothing to shorten")
        } */

    }
    for (newItem in newItems) {
        newItem.validFrom = newItem.validFrom?.toString()
        newItem.validTo = newItem.validTo?.toString()
        newItem.priceUnit = newItem.priceUnit?.toString()
        newItem.conditionValue = newItem.conditionValue?.toString()
        // Added by suganya to fix Black cat upgrade 16.1.0 issue
        api.addOrUpdate(typeCode,newItem)
      // conditionRecordHelper.addOrUpdate(typeCode,newItem)
       /* if(newItem != null
                && newItem.conditionRecordSetId != null
                && newItem.conditionValue != null
                && newItem.validFrom != null
                && newItem.validTo != null
                && newItem.attribute5 != null)
        {
            conditionRecordHelper.addOrUpdate([
                    key1: newItem.key1,
                    key2: newItem.key2,
                    key3: newItem.key3,
                    key4: newItem.key4,
                    key5: newItem.key5,
                    validTo: newItem.validTo.format("yyyy-MM-dd"),
                    validFrom: newItem.validFrom.format("yyyy-MM-dd"),
                    unitOfMeasure: newItem.unitOfMeasure,
                    priceUnit:newItem.priceUnit,
                    conditionValue: newItem.conditionValue,
                    currency: newItem.currency,
                    conditionRecordSetId: newItem.conditionRecordSetId,
                    attribute1: newItem.attribute1?.value,
                    attribute4: newItem.attribute4?.value,
                    attribute5: newItem.attribute5.value
            ])
        }
        else if (newItem != null
                && newItem.conditionRecordSetId != null
                && newItem.validFrom != null
                && newItem.validTo != null) {

            // Partial record — shorten validTo only, using today as the cutoff
            conditionRecordHelper.shortenValidTo([
                    key1:                 newItem.key1,
                    key2:                 newItem.key2,
                    key3:                 newItem.key3,
                    key4:                 newItem.key4,
                    key5:                 newItem.key5,
                    validFrom:            newItem.validFrom.format("yyyy-MM-dd"),
                    validTo:              new Date().format("yyyy-MM-dd"),  // shorten to today
                    conditionRecordSetId: newItem.conditionRecordSetId
            ])

        } else {
            // supersededItem is null or missing even the minimum fields to identify the record
            api.logInfo("CR ShortenValidTo Skipped",
                    "supersededItem is null or missing conditionRecordSetId/validFrom/validTo — nothing to shorten")
        } */


    }
}

def addPendingLines(List delayedLines) {
    def cptName = libs.QuoteConstantsLibrary.Tables.PENDING_CONDITION_RECORDS
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")

    def table
    if (api.local.isMassEdit || api.local.isPricingFormula) table = "A904"
    if (api.local.isListPriceZLIS) table = "A901"
    if (api.local.isPricelistZBPL) table = "A932"

    delayedLines.each { line ->
        buildRowToAddOrUpdate(ppId, line, table)
    }
}

private def buildRowToAddOrUpdate(ppId, line, table) {
    def key = line.key1 + "|" + line.key2 + "|" + line.key3 + "|" + line.key4 + "|" + line.key5 + "|" + line.validFrom + "|" + line.validTo
    def data = api.jsonEncode(line)

    def attributeExtension = [
            "Status": libs.QuoteLibrary.Calculations.PENDING_STATUS,
            "Data"  : data,
            "Table" : table
    ]

    def req = [data: [
            header: ['lookupTable', 'name', 'attributeExtension'],
            data : [[ppId, key, api.jsonEncode(attributeExtension)]]
    ]]

    def body = api.jsonEncode(req)?.toString()

    api.boundCall("SystemUpdate", "/loaddata/JLTV", body, false)
}


// This is to fix a bug since 16.1 upgrade
private List addLastModifiedByAndCreatedBy(List lines) {
    if (!lines) return lines
    lines?.collect {
        it["lastModifiedByToken"] = "testToken"
        it["createdByToken"] = "testToken"
        return it
    }
}