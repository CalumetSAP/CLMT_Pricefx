def getGlobalUOMConversion(){
    def globalUOMConversion = [:]
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.GLOBAL_UOM_CONVERSION)

    qapi.source(t1, [t1.key1(), t1.key2(), t1.ConversionFactor.as("attribute1")]).stream { it.collect() }?.each { it ->
        globalUOMConversion.putIfAbsent(it.key1, [:])
        globalUOMConversion.putIfAbsent(it.key2, [:])

        globalUOMConversion[it.key1][it.key2] = 1.0 /  it.attribute1?.toBigDecimal()
        globalUOMConversion[it.key2][it.key1] = it.attribute1?.toBigDecimal()
    }
    return globalUOMConversion
}

def getUOMConversion(materials, products){
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows(libs.QuoteConstantsLibrary.Tables.UOM_CONVERSION)

    def filter = materials ? t1.key1().in(materials) : t1.key1().isNotNull()

    def result = qapi.source(t1, [t1.key1(), t1.key2(), t1.Numerator.as("attribute1"), t1.Denominator.as("attribute2")], filter)
            .stream { it.collect() }

    def uomConversion = [:]
    result.each { it ->
        def material = it.key1
        def defaultUOM = products?.get(material)?.UOM
        if (!defaultUOM) return
        uomConversion.putIfAbsent(material, [:])
        uomConversion[material].putIfAbsent(defaultUOM, [:])
        uomConversion[material].putIfAbsent(it.key2, [:])

        def conversion = it.attribute1?.toBigDecimal() / it.attribute2?.toBigDecimal()

        uomConversion[material][defaultUOM][it.key2] = conversion
        uomConversion[material][it.key2][defaultUOM] = 1.0 / conversion
    }

    return uomConversion
}

def getConversionFactor(material, fromUOM, toUOM, uomConversions, globalUomConversion, visited = new HashSet<>()) {
    if (fromUOM == toUOM) return 1.0

    Map newConversionMap = [:]
    Map mapAux
    globalUomConversion?.each { from, map ->
        mapAux = [:]
        map.each { to, value ->
            mapAux.put(to, value)
        }
        newConversionMap.put(from, mapAux)
    }

    uomConversions[material].each { from, map ->
        map.each { to, factor ->
            newConversionMap?.putIfAbsent(from, [:])
            newConversionMap[from]?.putIfAbsent(to, factor)
        }
    }

    if (!newConversionMap.containsKey(fromUOM) || !newConversionMap.containsKey(toUOM)) return null

    def queue = []
    queue << [fromUOM, 1.0]

    (visited as HashSet).clear()

    while (!queue.isEmpty()) {
        def entry = queue.remove(0)
        String currentUOM = entry[0]
        def currentFactor = entry[1]
        visited.add(currentUOM)

        if (currentUOM == toUOM) return currentFactor

        newConversionMap[currentUOM].each { nextUOM, factor ->
            if (!visited.contains(nextUOM)) {
                queue << [nextUOM, currentFactor * factor]
            }
        }
    }


    return null
}

//def getConversionFactor(material, fromUOM, toUOM, uomConversions, globalUomConversion) {
//    if (!toUOM || !fromUOM) return null
//
//    if (fromUOM == toUOM) {
//        return 1.0
//    }
//
//    def conversionFactor = calculateGlobalUOMConversionFactor(fromUOM, toUOM, globalUomConversion)
//    if (conversionFactor) return conversionFactor
//
//    conversionFactor = calculateUOMConversionFactor(material, fromUOM, toUOM, uomConversions)
//    if (conversionFactor) return conversionFactor
//
//    conversionFactor = calculateGlobalToSpecificUOMConversionFactor(material, fromUOM, toUOM, globalUomConversion, uomConversions)
//    if (conversionFactor) return conversionFactor
//
//    conversionFactor = calculateSpecificToGlobalUOMConversionFactor(material, fromUOM, toUOM, globalUomConversion, uomConversions)
//
//    return conversionFactor
//}