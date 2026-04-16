import net.pricefx.server.dto.calculation.DashboardController

def configurator = out.Filters

def constants = libs.DashboardConstantsLibrary.PricePublishing

//def filters = [
//        Filter.equal("lookupTable.name", "VariantsPBPricePublishingDashboard")
//]
//
//if(!api.isDebugMode()) {
//    filters.add(Filter.equal("name", configurator[constants.VARIANT_INPUT_KEY]?:configurator[constants.VARIANT_NAME_INPUT_KEY]))
//}

def qapi = api.queryApi()

def t1 = qapi.tables().companyParameterRows("VariantsPBPricePublishingDashboard")
def fields = [t1.key1()]
fields += libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_COLUMNS.collect { t1."${it}" }
fields += libs.DashboardConstantsLibrary.PricePublishing.ENCODED_PB_VARIANT_INPUT_COLUMNS.collect { t1."${it}" }

def filter
if(!api.isDebugMode()) {
    def variantName = configurator[constants.VARIANT_INPUT_KEY] ?: (configurator[constants.VARIANT_NAME_INPUT_KEY] ?: "")
    filter = qapi.exprs().and(t1.key1().equal(variantName))
}

def result = qapi.source(t1, fields, filter).stream { it.collect { it } }.find()

//def result = api.find("JLTV", 0, 1, "name", ["name", *libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_COLUMNS, *libs.DashboardConstantsLibrary.PricePublishing.ENCODED_PB_VARIANT_INPUT_COLUMNS], *filters)
//        .find{it}

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

DashboardController controller = api.newController()

def changedVariantInputs = []

def tableName = "VariantsPBPricePublishingDashboard"

def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", tableName))?.first()?.get("id")

def attributeExtension = [:]

for(variant in libs.DashboardConstantsLibrary.PricePublishing.VARIANT_INPUT_COLUMNS) {
    if(api.jsonEncode(decodedMap?.get(variant)) != api.jsonEncode(configurator?.get(variant))){
        changedVariantInputs.add(variant)
    }
    attributeExtension[variant] = configurator?.get(variant)
}

for(variant in libs.DashboardConstantsLibrary.PricePublishing.ENCODED_PB_VARIANT_INPUT_COLUMNS) {
    if(api.jsonEncode(decodedMap?.get(variant)) != api.jsonEncode(configurator?.get(variant))){
        changedVariantInputs.add(variant)
    }
    if(configurator?.get(variant)) {
        attributeExtension[variant] = api.jsonEncode(configurator?.get(variant))
    }
}

def previousVariantName = decodedMap?.get("key1")
def newVariantName = configurator?.get(constants.VARIANT_NAME_INPUT_KEY)
def variantNameChanged = (newVariantName && !previousVariantName) || newVariantName != previousVariantName

def req = [data: [
        header: ['lookupTable', 'name', 'attributeExtension'],
        data : [[ppId, configurator?.get(constants.VARIANT_NAME_INPUT_KEY), api.jsonEncode(attributeExtension)]]
]]

def body = api.jsonEncode(req)?.toString()


if(newVariantName && !variantNameChanged && changedVariantInputs){
        controller.addBackendCall("Save Variant", "formulamanager.executeformula/LoadVariantsLogic", body,
            "Saved",
            "Fail")
        // Saving Variant will override existing variant filters
        controller.addHTML("""<span style='color:red;font-weight:bold;'>Saving Variant will override existing variant filters</span>""")
}

if(variantNameChanged){
        controller.addBackendCall("Save Variant", "formulamanager.executeformula/LoadVariantsLogic", body,
            "Saved",
            "Fail")
        // Saving Variant will create variant "XXX
        controller.addHTML("""<span style='color:red;font-weight:bold;'>Saving Variant will create variant "${newVariantName}"</span>""")
}

return controller