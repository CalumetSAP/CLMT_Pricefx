def variants = out.FindVariants?.collect{it.key1}

def entry = libs.BdpLib.UserInputs.createInputOption(
        libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_LABEL,
        false,
        false,
        [libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL, *variants],
        null,
        {it ->
            api.global.variantChanged = true
        }
)

if(api.global.variantChanged || entry.getFirstInput().getValue()) {

    def qapi = api.queryApi()

    def t1 = qapi.tables().companyParameterRows("VariantsPricePublishingDashboard")
    def fields = [t1.key1()]
    fields += libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_COLUMNS.collect { t1."${it}" }
    fields += libs.DashboardConstantsLibrary.PricePublishing.ENCODED_VARIANT_INPUT_COLUMNS.collect { t1."${it}" }

    def filter = qapi.exprs().and(t1.key1().equal(entry.getFirstInput().getValue() ?: ""))

    def result = qapi.source(t1, fields, filter).stream { it.collect { it } }.find()

//    def filters = [
//            Filter.equal("lookupTable.name", "VariantsPricePublishingDashboard"),
//            Filter.equal("name", entry.getFirstInput().getValue())
//    ]
//
//    def result = api.find("JLTV", 0, 1, "name", ["name", *libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_COLUMNS, *libs.DashboardConstantsLibrary.PricePublishing.ENCODED_VARIANT_INPUT_COLUMNS], *filters)
//            .find{it}

    def decodedMap = [:]

    result.each{ key, value ->
        if(value){
            try{
                try {
                    decodedMap[key as String] = api.jsonDecode(value?.toString())
                } catch(error2) {
                    decodedMap[key as String] = api.jsonDecodeList(value?.toString())
                }
            } catch(error) {
                decodedMap[key as String] = value
            }
        }

    }

    api.global.selectedVariant = decodedMap
}

return entry