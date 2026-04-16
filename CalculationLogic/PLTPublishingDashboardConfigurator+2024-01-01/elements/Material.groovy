def entry = api.createConfiguratorEntry()

def params = [
        "PricingDate": out.PricingDate.getFirstInput()?.getValue(),
        "Pricelist"  : out.Pricelist.getFirstInput()?.getValue(),
        "Brand"      : out.Brand.getFirstInput()?.getValue()
]

def filterFormulaParams = api.jsonEncode(params)

def param = api.inputBuilderFactory()
        .createProductGroupEntry(libs.DashboardConstantsLibrary.PLTDashboard.MATERIAL_INPUT_KEY)
        .setLabel(libs.DashboardConstantsLibrary.PLTDashboard.MATERIAL_INPUT_LABEL)
        .setRequired(false)
        .setReadOnly(false)
        .setFilterFormulaName("PLTPublishingProductFilter")
        .setFilterFormulaParam(filterFormulaParams as String)
        .buildContextParameter()

entry.createParameter(param)

return entry