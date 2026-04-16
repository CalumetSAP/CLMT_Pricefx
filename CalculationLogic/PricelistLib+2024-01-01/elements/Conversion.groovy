def getGlobalUOMConversion(){
    def globalUOMConversion = [:]
    api.findLookupTableValues("GlobalUOMConversion")?.each { it ->
        def key = it.key1 + "|" + it.key2
        globalUOMConversion[key] = it.attribute1?.toBigDecimal()
    }
    return globalUOMConversion
}

def getUOMConversion(materials, products){
    def fields = ['key1', 'key2', 'attribute1', 'attribute2']
    def result = api.findLookupTableValues("UOMConversion", fields, "key1", Filter.in("key1", materials))

    def uomConversion = [:]
    result.each { it ->
        def material = it.key1
        def key = material + "|" + products?.get(material)?.UOM + "|" + it.key2
        uomConversion[key] = [
                numerat : it.attribute1?.toBigDecimal(),
                denom   : it.attribute2?.toBigDecimal()
        ]
    }

    def availableUom = []
    result.each { materialItem ->
        def material = materialItem.key1
        def aUnOriginal = materialItem.key2
        def fromUn = products?.get(material)?.UOM

        def combinedUoms = ([fromUn] + [aUnOriginal]).unique()

        combinedUoms.each { aUn ->
            availableUom << ["material": material, "aUn": aUn]
        }
    }
    availableUom = availableUom?.unique()?.groupBy { it.material }

    def conversion = [
            "uomConversion": uomConversion,
            "availableUom": availableUom,
    ]

    return conversion
}

def getConversionFactor(material, fromUOM, toUOM, uomConversions, globalUomConversion) {
    if (!toUOM || !fromUOM) return null

    if (fromUOM == toUOM) {
        return 1.0
    }

    def conversionFactor = calculateGlobalUOMConversionFactor(fromUOM, toUOM, globalUomConversion)
    if (conversionFactor) return conversionFactor

    conversionFactor = calculateUOMConversionFactor(material, fromUOM, toUOM, uomConversions)

    return conversionFactor
}

def getPricingConversionFactor (material, fromUOM, toUOM, uomConversions, globalUOMConversions) {
    if (!toUOM || !fromUOM) return null

    if (fromUOM == toUOM) {
        return 1.0
    }

    //FIRST TRY: find in the uomConversion with initial UOMs
    def conversion = calculateUOMConversionFactor(material, fromUOM, toUOM, uomConversions)
    if (conversion) {
        return conversion
    }

    //SECOND TRY: find global conversions that fit in the uomConversion for the fromUOM and toUOM
    //Get all the conversions with the toUOM
    def alternativeFromUOMs = uomConversions.uomConversion.keySet()?.findAll { key ->
        ((key.startsWith(material + "|" + toUOM + "|") || (key.startsWith(material + "|") && (key.endsWith("|" + toUOM))))
                && key != material + "|" + toUOM + "|" + toUOM)
    }?.collect { it.split("\\|") }?.flatten()?.toSet()
    alternativeFromUOMs.remove(material)
    alternativeFromUOMs.remove(toUOM)
    def alternativeFromUOMConversion = [:]
    def alternativeConversion
    for (alternativeUOM in alternativeFromUOMs) {
        alternativeConversion = calculateGlobalUOMConversionFactor(fromUOM, alternativeUOM, globalUOMConversions)
        if (alternativeConversion) {
            alternativeFromUOMConversion = [
                    alternativeUOM          : alternativeUOM,
                    alternativeConversion   : alternativeConversion,
            ]
            break
        }
    }
    if (alternativeFromUOMConversion) {
        conversion = calculateUOMConversionFactor(material, alternativeFromUOMConversion.alternativeUOM, toUOM, uomConversions)
        if (conversion) {
            return conversion * alternativeFromUOMConversion.alternativeConversion
        }
    }

    //THIRD TRY: convert using the globalUOMConversion CPT
    conversion = calculateGlobalUOMConversionFactor(fromUOM, toUOM, globalUOMConversions)
    if (conversion) {
        return conversion
    }

    return null
}

def calculateGlobalUOMConversionFactor(fromUOM, toUOM, globalUomConversion) {
    def directFactor = globalUomConversion[fromUOM + "|" + toUOM]
    if (!directFactor) {
        def indirectFactor = globalUomConversion[toUOM + "|" + fromUOM]
        directFactor = indirectFactor ? 1 / indirectFactor : null
    }
    if (directFactor) return 1 / directFactor

    def conversion = null
    def potentialIntermediates = globalUomConversion.keySet().findAll {
        it.startsWith(fromUOM + "|") || it.endsWith("|" + fromUOM)
    }.collect { it.replace(fromUOM + "|", "").replace("|" + fromUOM, "") }
            .findAll { intermediate ->
                globalUomConversion.containsKey(intermediate + "|" + toUOM) || globalUomConversion.containsKey(toUOM + "|" + intermediate)
            }

    potentialIntermediates.each { intermediateUOM ->
        def firstLevelFactor = globalUomConversion[fromUOM + "|" + intermediateUOM] ?: (globalUomConversion[intermediateUOM + "|" + fromUOM] ? 1 / globalUomConversion[intermediateUOM + "|" + fromUOM] : null)
        def secondLevelFactor = globalUomConversion[intermediateUOM + "|" + toUOM] ?: (globalUomConversion[toUOM + "|" + intermediateUOM] ? 1 / globalUomConversion[toUOM + "|" + intermediateUOM] : null)
        if (firstLevelFactor && secondLevelFactor) {
            conversion = 1 / (firstLevelFactor * secondLevelFactor)
        }
    }
    if(conversion) return conversion

    return null
}

def calculateUOMConversionFactor(material, fromUOM, toUOM, uomConversions) {
    def uomConversionKey = material + "|" + fromUOM + "|" + toUOM
    def reverseUomConversionKey = material + "|" + toUOM + "|" + fromUOM

    def materialConversion = uomConversions?.uomConversion[uomConversionKey]
    if (materialConversion) {
        return materialConversion.numerat / materialConversion.denom
    }

    def reverseMaterialConversion = uomConversions?.uomConversion[reverseUomConversionKey]
    if (reverseMaterialConversion) {
        return reverseMaterialConversion.denom / reverseMaterialConversion.numerat
    }

    def conversion = findIndirectConversion(material, fromUOM, toUOM, uomConversions?.uomConversion)
    if (conversion) return conversion

    return null
}

def findIndirectConversion(material, fromUOM, toUOM, conversions) {
    def potentialIntermediates = conversions.keySet().findAll { key ->
        key.startsWith(material + "|") && (key.endsWith("|" + fromUOM) || key.endsWith("|" + toUOM))
    }.collect { key ->
        def parts = key.split(/\|/)
        return parts[1] == fromUOM || parts[1] == toUOM ? parts[2] : parts[1]
    }.unique()

    def conversionResult = null
    potentialIntermediates.find { intermediateUOM ->
        def firstLevelConversion = conversions[material + "|" + fromUOM + "|" + intermediateUOM]
        def secondLevelConversion = conversions[material + "|" + intermediateUOM + "|" + toUOM]

        if (!firstLevelConversion) {
            firstLevelConversion = conversions[material + "|" + intermediateUOM + "|" + fromUOM]
            if (firstLevelConversion) {
                firstLevelConversion = [numerat: firstLevelConversion.denom, denom: firstLevelConversion.numerat]
            }
        }
        if (!secondLevelConversion) {
            secondLevelConversion = conversions[material + "|" + toUOM + "|" + intermediateUOM]
            if (secondLevelConversion) {
                secondLevelConversion = [numerat: secondLevelConversion.denom, denom: secondLevelConversion.numerat]
            }
        }

        if (firstLevelConversion && secondLevelConversion) {
            def firstLevelFactor = firstLevelConversion.numerat / firstLevelConversion.denom
            def secondLevelFactor = secondLevelConversion.numerat / secondLevelConversion.denom
            conversionResult = libs.SharedLib.RoundingUtils.round(firstLevelFactor * secondLevelFactor, 4)
            return true
        }
        return false
    }

    return conversionResult
}

BigDecimal getKMToMilesConversionFactor () {
    def qapi = api.queryApi()
    def t1 = qapi.tables().companyParameterRows("FreightBusinessRules")
    def filter = qapi.exprs().and(
            t1.Field.equal("ConversionFactor"),
            t1.Code.equal("KMToMiles")
    )
    return qapi.source(t1, [t1.Value], filter).stream { it.collect().find().Value.toBigDecimal() }
}