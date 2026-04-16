/**
 * Prepares a list of keys1 and possibly a list of keys2 of items being calculated in the current batch.
 * Internally caches this to api.global.currentBatch, api.global.batchKeys1, api.global.batchKeys2, api.local.isNewBatch, api.local.isEndBatch and api.global.iterationNumber.
 * This function should be called prior any batch getter function is called ensuring retainGlobal is set to true.
 * Works automatically with simple or matrix logic as well as with CFS.
 * Works even if api.getBatchInfo() is not available. In this case sets a batch of forwarded item only.
 * @param skuOrId Use id or typedId in the CFS context otherwise use sku.
 * @param key2 Use key2 from matrix logic otherwise leave null.
 * @param isTypedId Set to true if function should parse id from typedId.
 * @return Returns set of IDs in CFS context or SKUs in simple logic or pairs of [sku, key2] in matrix logic.
 */
Set prepareBatch(String skuOrId, String key2 = null, Boolean isTypedId = false) {
    def currentItemBatchKeys = parseCurrentItemBatchKeys(skuOrId, isTypedId, key2)

    //every new pass is a new batch also
    def iterationNumber = api.getIterationNumber()

    checkIfNewBatch(currentItemBatchKeys, iterationNumber)
    if (isNewBatch()) {
        clearBatch(iterationNumber)

        def batchInfo = api.getBatchInfo()
        if (batchInfo) {
            loadSystemBatch(batchInfo, key2)
        } else {
            mockOneElementBatch(currentItemBatchKeys)
        }
    }

    checkIfEndBatch(currentItemBatchKeys)

    return api.global.currentBatch
}


/**
 * Elements can call this function to check whether a new batched database query for a new set of items should be performed (rather than access api.local.isNewBatch directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called.
 * @return Returns true whether a new batch of items is being calculated (calculated item is first in the batch)
 */
boolean isNewBatch() {
    return api.local.isNewBatch
}

/**
 * Elements can call this function to check whether needs to perform any action at the end of a batch (rather than access api.local.isEndBatch directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called.
 * @return Returns true whether the last item in current batch is being calculated
 */
boolean isEndBatch() {
    return api.local.isEndBatch
}

/**
 * Elements can call this function to get a set of keys1 in current batch to be used in a new batched database query (rather than access api.global.batchKeys1 directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called.
 * Key can be ID in CFS context or SKU otherwise. In simple logic the list is the same as in {@link #getCurrentBatch()}.
 * @return Returns a set of keys1.
 */
Set getCurrentBatchKeys1() {
    return api.global.batchKeys1 as Set
}

/**
 * *Warning* This function, despite its name, do not return skus. In case of CFS calculation, id will be returned.
 * *Warning* Check libs.SharedAccLib.BatchUtils.getBatchedSkus for function always returning sku.
 * Prettier function name in contexts with SKU as key1. Returns the same value as {@link #getCurrentBatchKeys1()}.
 * Elements can call this function to get a set of SKUs in current batch to be used in a new batched database query (rather than access api.global.batchKeys1 directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called.
 * @return Returns a set of keys1.
 */
Set getCurrentBatchSku() {
    return getCurrentBatchKeys1()
}

/**
 * Prettier function name in CFS context with IDs as key1. Returns the same value as {@link #getCurrentBatchKeys1()}.
 * Elements can call this function to get a set of SKUs in current batch to be used in a new batched database query (rather than access api.global.batchKeys1 directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called.
 * @return Returns a set of keys1.
 */
Set getCurrentBatchIds() {
    return getCurrentBatchKeys1()
}

/**
 * Elements can call this function to get a set of keys2 in current batch to be used in a new batched database query (rather than access api.global.batchKeys2 directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called.
 * @return Returns a set of keys2 in matrix logic otherwise returns null.
 */
Set getCurrentBatchKeys2() {
    return api.global.batchKeys2 as Set
}

/**
 * Elements can call this function to get current batch values to be used in a new batched database query (rather than access api.global.currentBatch directly).
 * This function can be called once the {@link #prepareBatch(String skuOrTypedId, String key2 = null, Boolean skuWithDot = false)} has been called and returns the same value.
 * @return Returns a set of IDs in CFS context or SKUs in simple logic or pairs of [sku, key2] in matrix logic.
 */
Set getCurrentBatch() {
    return api.global.currentBatch
}

protected def parseCurrentItemBatchKeys(String skuOrId, boolean isTypedId, String key2) {
    String key1 = skuOrId

    //input id is typedId
    if (isTypedId) {
        def typedId = skuOrId.split("\\.")
        if (typedId.size() > 1) {
            key1 = typedId[0]
        }
    }

    //if we use matrix logic we need to use compound batch item as well
    def currentItemBatchKeys = key2 ? [key1, key2] : key1

    return currentItemBatchKeys
}

protected void checkIfNewBatch(def currentItemBatchKeys, int iterationNumber) {
    api.local.isNewBatch = (api.global.currentBatch == null || !api.global.currentBatch.contains(currentItemBatchKeys) || api.global.iterationNumber != iterationNumber)
}

protected void clearBatch(int iterationNumber) {
    api.global.iterationNumber = iterationNumber
    api.global.batchKeys1 = null
    api.global.batchKeys2 = null
}

protected void loadSystemBatch(List batchInfo, String key2) {
    api.global.batchKeys1 = batchInfo.collect { it.first() }
    if (key2) {
        api.global.batchKeys2 = batchInfo.collect { it.last() }
        api.global.currentBatch = batchInfo.collect { [it[0], it[1]] }
    } else {
        api.global.currentBatch = api.global.batchKeys1
    }
}

protected void mockOneElementBatch(def currentItemBatchKeys) {
    Map keys = getMockedKeys(currentItemBatchKeys)

    api.global.currentBatch = [currentItemBatchKeys]
    api.global.batchKeys1 = [keys.key1]
    if (keys.key2) {
        api.global.batchKeys2 = [keys.key2]
    }
}

protected Map getMockedKeys(def currentItemBatchKeys) {
    Map returnValue = [:]
    if (currentItemBatchKeys instanceof List) {
        returnValue.key1 = currentItemBatchKeys[0]
        returnValue.key2 = currentItemBatchKeys[1]
    } else {
        returnValue.key1 = currentItemBatchKeys
    }

    return returnValue
}

protected void checkIfEndBatch(def currentItemBatchKeys) {
    api.local.isEndBatch = (api.global.currentBatch && api.global.currentBatch.last() == currentItemBatchKeys)
}
