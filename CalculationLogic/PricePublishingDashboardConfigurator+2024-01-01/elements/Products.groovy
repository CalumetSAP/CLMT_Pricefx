
def entry = api.createConfiguratorEntry()

def params = [
        "PH1": out.PH1.getFirstInput().getValue(),
        "PH2": out.PH2.getFirstInput().getValue(),
        "PH3": out.PH3.getFirstInput().getValue(),
        "PH4": out.PH4.getFirstInput().getValue(),
        "Brand": out.Brand.getFirstInput().getValue()
]

def filterFormulaParams = api.jsonEncode(params)

def param = api.inputBuilderFactory()
        .createProductGroupEntry(libs.DashboardConstantsLibrary.PricePublishing.PRODUCTS_INPUT_KEY)
        .setLabel(libs.DashboardConstantsLibrary.PricePublishing.PRODUCTS_INPUT_LABEL)
        .setRequired(false)
        .setReadOnly(false)
        .setFilterFormulaName("PricePublishingProductFilter")
        .setFilterFormulaParam(filterFormulaParams as String)
        .buildContextParameter()

def variantProducts = api.global.selectedVariant?.Products

entry.createParameter(param)

if((api.global.variantChanged && variantProducts) || (!entry.getFirstInput().getValue() && variantProducts)) {
    entry.getFirstInput().setValue(variantProducts)
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry