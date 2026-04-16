
def ph1 = out.PH1?.getFirstInput()?.getValue()
def ph2 = out.PH2?.getFirstInput()?.getValue()
def ph3 = out.PH3?.getFirstInput()?.getValue()
def ph4 = out.PH4?.getFirstInput()?.getValue()


def filters = [
        Filter.isNotEmpty("attribute2")
]

if(ph1) filters.add(Filter.in("attribute14", ph1))
if(ph2) filters.add(Filter.in("attribute16", ph2))
if(ph3) filters.add(Filter.in("attribute18", ph3))
if(ph4) filters.add(Filter.in("attribute20", ph4))


def brands = api.stream("P", "attribute2", ["attribute2"], true, *filters)?.withCloseable { it.collect{ it.attribute2 } }?.findAll{it}
def variantBrands = api.global.selectedVariant?.Brand

def entry = libs.BdpLib.UserInputs.createInputOptions(
        libs.DashboardConstantsLibrary.PricePublishing.BRAND_INPUT_KEY,
        libs.DashboardConstantsLibrary.PricePublishing.BRAND_INPUT_LABEL,
        false,
        false,
        null,
        brands
)

if((api.global.variantChanged && variantBrands) || (!entry.getFirstInput().getValue() && variantBrands)) {
    entry.getFirstInput().setValue(variantBrands?.unique())
}

if(out.Variant?.getFirstInput()?.getValue() == libs.DashboardConstantsLibrary.PricePublishing.CLEAR_ALL) {
    entry.getFirstInput().setValue()
}

return entry