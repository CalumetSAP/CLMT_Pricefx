import net.pricefx.formulaengine.XExpression
import com.googlecode.genericdao.search.Filter //Test coverage requires compile step -> compile step requires this

/**
 * To look through records of PP, it's ID is needed. In configuration files, we need to fetch this id.
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param ppName Name of Price Parameter
 * @return ID of Price Parameter
 */
String getPPIdFromName(String ppName) {
    String key = buildCacheKey("LT", ppName)
    return libs.SharedLib.CacheUtils.getOrSet(key, [ppName], { _ppName ->
        String today = api.calendar().getTime().format("yyyy-MM-dd")
        List<Filter> ltFilters = [Filter.equal("uniqueName", _ppName),
                                  Filter.equal("status", "ACTIVE"),
                                  Filter.lessOrEqual("validAfter", today)]

        return api.find("LT", 0, 1, "-validAfter", ["id"], *ltFilters)?.getAt(0)?.id
    })
}

/**
 * Function used for finding id of a PP column, based on label from metadata (name in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param lookupTableIdOrName - function supports calling it with ID of PP or its name
 * @param label - label of column in metadata
 * @return attributeId of column
 */
String getLTVAttributeId(String lookupTableIdOrName, String label) {
    String lookupTableId = getPPIdFromName(lookupTableIdOrName) ?: lookupTableIdOrName
    if (!lookupTableId.isLong()) {
        return
    }
    String key = buildCacheKey("LTV", label, lookupTableId)
    return libs.SharedLib.CacheUtils.getOrSet(key, [label, lookupTableId], { String _label, String _lookupTableId ->
        return findMLTVM(_lookupTableId, _label)
    })
}

/**
 * Function used for finding id of a PX column, based on label from metadata (name in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param pxName - function supports calling it with Product Extension name. PX label is not supported currently
 * @param label - label of column in metadata
 * @return attributeId of column
 */
String getPXAttributeId(String pxName, String label) {
    String key = buildCacheKey("PX", label, pxName)
    return libs.SharedLib.CacheUtils.getOrSet(key, [label, pxName], { String _label, String _pxName ->
        return findPXAM(_pxName, _label)
    })
}
/**
 * Function used for finding id of a CX column, based on label from metadata (name in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param cxName - function supports calling it with Customer Extension name. CX label is not supported currently
 * @param label - label of column in metadata
 * @return attributeId of column
 */
String getCXAttributeId(String cxName, String label) {
    String key = buildCacheKey("CX", label, cxName)
    return libs.SharedLib.CacheUtils.getOrSet(key, [label, cxName], { String _label, String _cxName ->
        return findCXAM(_cxName, _label)
    })
}

/**
 * Function used for finding id of a PGI column, based on label from metadata (name in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param priceGridId - function supports calling it with price grid id. Price grid name is not unique and shall never be supported
 * @param elementNameOrLabel - label of column in metadata or element name in logic
 * @return attributeId of column
 */
String getPGIAttributeId(String priceGridId, String elementNameOrLabel) {
    String key = buildCacheKey("PGI", elementNameOrLabel, priceGridId)
    return libs.SharedLib.CacheUtils.getOrSet(key, [elementNameOrLabel, priceGridId], { String _elementNameOrLabel, String _priceGridId ->
        return findPGIM(_priceGridId, _elementNameOrLabel)
    })
}

/**
 * Function used for finding id of a PLI column, based on label from metadata (name in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param priceListId - function supports calling it with price list id. Price list name is not unique and shall never be supported
 * @param elementNameOrLabel - label of column in metadata or element name in logic
 * @return attributeId of column
 */
String getPLIAttributeId(String priceListId, String elementNameOrLabel) {
    String key = buildCacheKey("PLI", elementNameOrLabel, priceListId)
    return libs.SharedLib.CacheUtils.getOrSet(key, [elementNameOrLabel, priceListId], { String _elementNameOrLabel, String _priceListId ->
        return findPLIM(_priceListId, _elementNameOrLabel)
    })
}

/**
 * Function used for finding id of Product collum, based on label from metadata(name in GUI)
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param label - label of column in in product metadata
 * @return attribute of column
 */
String getPAttributeId(String label) {
    String key = buildCacheKey("PAM", label)
    return libs.SharedLib.CacheUtils.getOrSet(key, [label], { _label -> findPAMAttributeId(_label) })
}
/**
 * Function used for finding id of Customer column, based on label from metadata(name in GUI)
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param label - label of column in in customer metadata
 * @return attribute of column
 */
String getCAttributeId(String label) {
    String key = buildCacheKey("CAM", label)
    return libs.SharedLib.CacheUtils.getOrSet(key, [label], { _label -> findCAMAttributeId(_label) })
}

/**
 * Function used as dynamical way to call
 * - getLTVAttributeId
 * - getPXAttributeId
 * - getPGIAttributeId
 * - getPLIAttributeId
 * - getPAttributeId
 * @param type - type of data, which attribute you want to translate
 * @param containerIdOrName - The first input parameter for methods listed above
 * @param label - The second input parameter for methods listed above
 * @return attributeId of column
 * @throws XExpression when type is not supported
 */
String getAttributeIdFromLabel(String type, String containerIdOrName, String label) throws XExpression {
    switch (type) {
        case "LTV":
            return getLTVAttributeId(containerIdOrName, label)
        case "PX":
            return getPXAttributeId(containerIdOrName, label)
        case "CX":
            return getCXAttributeId(containerIdOrName, label)
        case "PGI":
            return getPGIAttributeId(containerIdOrName, label)
        case "PLI":
            return getPLIAttributeId(containerIdOrName, label)
        case "P":
            return getPAttributeId(label)
        case "C":
            return getCAttributeId(label)
    }
    api.throwException("Unsupported type $type")
}

/**
 * Function used for finding ids of a PGI columns, based on labels from metadata (names in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param priceGridtId - function supports calling it with price grid id. Price grid name is not unique and shall never be supported
 * @param elementNamesOrLabels - labels of columns in metadata or element names in logic (may be mixed)
 * @return List of maps, each consisting 3 keys: "fieldName", "label", "elementName"
 */
List<Map<String, String>> getPGIAttributeIdBatched(String priceGridId, List elementNamesOrLabels) {
    String key = buildCacheKey("PGI", elementNamesOrLabels as String, priceGridId)
    return libs.SharedLib.CacheUtils.getOrSet(key, [elementNamesOrLabels, priceGridId], { List _elementNamesOrLabels, String _priceGridId ->
        return findPGIMBatched(_priceGridId, _elementNamesOrLabels)
    })
}

/**
 * Function used for finding ids of a PLI columns, based on labels from metadata (names in GUI).
 * Results use cache in api.global - api.retainGlobal() should improve performance
 * @param priceListId - function supports calling it with price list id. Price list name is not unique and shall never be supported
 * @param elementNamesOrLabels - labels of columns in metadata or element names in logic (may be mixed)
 * @return List of maps, each consisting 3 keys: "fieldName", "label", "elementName"
 */
List<Map<String, String>> getPLIAttributeIdBatched(String priceListId, List elementNamesOrLabels) {
    String key = buildCacheKey("PLI", elementNamesOrLabels as String, priceListId)
    return libs.SharedLib.CacheUtils.getOrSet(key, [elementNamesOrLabels, priceListId], { List _elementNamesOrLabels, String _priceListId ->
        return findPLIMBatched(_priceListId, _elementNamesOrLabels)
    })
}

protected String buildCacheKey(String type, String label, String precision = null) {
    return precision ? ("DataUtils-${type}-${label}-${precision}" as String) : ("DataUtils-${type}-${label}" as String)
}

protected String findMLTVM(String lookupTableId, String label) {
    List filters = [Filter.equal("label", label), Filter.equal("lookupTableId", lookupTableId)]
    return api.find("MLTVM", 0, 1, null, ["fieldName"], *filters)?.getAt(0)?.fieldName
}

protected String findPXAM(String pxName, String label) {
    List filters = [Filter.equal("label", label), Filter.equal("name", pxName)]
    return api.find("PXAM", 0, 1, null, ["fieldName"], *filters)?.getAt(0)?.fieldName
}

protected String findCXAM(String cxName, String label) {
    List filters = [Filter.equal("label", label), Filter.equal("name", cxName)]
    return api.find("CXAM", 0, 1, null, ["fieldName"], *filters)?.getAt(0)?.fieldName
}

protected String findPGIM(String priceGridId, String labelOrElementName) {
    List filters = [Filter.equal("priceGridId", priceGridId),
                    Filter.or(
                            Filter.equal("label", labelOrElementName),
                            Filter.equal("elementName", labelOrElementName)
                    )
    ]
    return api.find("PGIM", 0, 1, null, ["fieldName"], *filters)?.getAt(0)?.fieldName
}

protected String findPLIM(String priceGridId, String labelOrElementName) {
    List filters = [Filter.equal("pricelistId", priceGridId),
                    Filter.or(
                            Filter.equal("label", labelOrElementName),
                            Filter.equal("elementName", labelOrElementName)
                    )
    ]
    return api.find("PLIM", 0, 1, null, ["fieldName"], *filters)?.getAt(0)?.fieldName
}

protected List<Map<String, String>> findPGIMBatched(String priceGridId, List labelsOrElementNames) {
    List filters = [Filter.equal("priceGridId", priceGridId),
                    Filter.or(
                            Filter.in("label", labelsOrElementNames),
                            Filter.in("elementName", labelsOrElementNames)
                    )
    ]
    return api.find("PGIM", 0, 0, null, ["fieldName", "label", "elementName"], *filters)
}

protected List<Map<String, String>> findPLIMBatched(String priceGridId, List labelsOrElementNames) {
    List filters = [Filter.equal("pricelistId", priceGridId),
                    Filter.or(
                            Filter.in("label", labelsOrElementNames),
                            Filter.in("elementName", labelsOrElementNames)
                    )
    ]
    return api.find("PLIM", 0, 0, null, ["fieldName", "label", "elementName"], *filters)
}

protected String findPAMAttributeId(String label) {
    def filter = Filter.equal("label", label)

    return api.find("PAM", 0, 1, null, ["fieldName"], filter)?.getAt(0)?.fieldName
}

protected String findCAMAttributeId(String label) {
    def filter = Filter.equal("label", label)

    return api.find("CAM", 0, 1, null, ["fieldName"], filter)?.getAt(0)?.fieldName
}
