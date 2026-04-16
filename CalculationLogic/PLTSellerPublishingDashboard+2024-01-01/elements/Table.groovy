import net.pricefx.common.api.FieldFormatType

def constants = libs.DashboardConstantsLibrary.PLTDashboard

def products = api.global.products
def pricingData = api.global.pricingData
def uomDescription = api.global.uomDescription

def configurationBreakdownReference = api.global.configurationBreakdownReference

def warnings = []

def columnLabels = [
        MaterialNumber     : constants.MATERIAL_NUMBER,
        MaterialDescription: constants.MATERIAL_DESCRIPTION,
        ItemIncluded       : constants.ITEM_INCLUDED,
        LegacyPartNumber   : constants.LEGACY_PART_NUMBER_EA,
        MOQPerUOM          : constants.MOQ_PER_UOM,
]

def summaryRows = []

def exportPdf = []

def pricelists = api.global.ZBPLmerge
def row, exportRow, value, material, materialDescription, itemIncluded, moqPerUOM
def productPM, legacyPart, pricingItem, pricelist, itemIncludedList, moq, moqUOM, moqUOMPdfExport, brand,
        productHierarchy1, productHierarchy2, productHierarchy3, productHierarchy4, moqPerUOMPdfExport,
        productHierarchy2Desc, productHierarchy3Desc
for (entry in pricelists.entrySet()) {
    row = [:]
    exportRow = [:]

    value = entry.value

    material = value?.Material
    productPM = products?.find{product -> product.sku == material}

    materialDescription = productPM?.label
    legacyPart = productPM?.attribute12?.toString()
    brand = productPM?.attribute2
    productHierarchy1 = productPM?.attribute14
    productHierarchy2 = productPM?.attribute16
    productHierarchy2Desc = productPM?.attribute17
    productHierarchy3 = productPM?.attribute18
    productHierarchy3Desc = productPM?.attribute19
    productHierarchy4 = productPM?.attribute20

    itemIncludedList = configurationBreakdownReference?.get(material) ?: []
    itemIncluded = itemIncludedList?.join(" ")

    pricelist = value?.Pricelist
    pricingItem = pricingData?.get(material + "|" + pricelist)

    (row[columnLabels."MaterialNumber"] = material)
    (row[columnLabels."MaterialDescription"] = materialDescription)
    (row[columnLabels."ItemIncluded"] = itemIncluded)
    (row[columnLabels."LegacyPartNumber"] = legacyPart)

    (exportRow["brand"] = brand ?: "")
    (exportRow["ph1"] = productHierarchy1 ?: "")
    (exportRow["ph2"] = productHierarchy2 ?: "")
    (exportRow["ph2Desc"] = productHierarchy2Desc ?: "")
    (exportRow["ph3Desc"] = productHierarchy3Desc ?: "")
    (exportRow["ph3"] = productHierarchy3 ?: "")
    (exportRow["ph4"] = productHierarchy4 ?: "")
    (exportRow["material"] = material ?: "")
    (exportRow["materialDescription"] = materialDescription ?: "")
    (exportRow["itemsIncluded"] = itemIncluded ?: "")
    (exportRow["legacyPartNo"] = legacyPart ?: "")

    moq = pricingItem?.MOQ ?: 1
    moqUOM = pricingItem?.MOQUOM ?: value?.UnitOfMeasure

    moqPerUOM = moq && moqUOM ? api.formatNumber("###,###.##", moq) + " / " + moqUOM : null

    moqUOMPdfExport = uomDescription?.get(moqUOM) ? uomDescription?.get(moqUOM) : moqUOM
    moqPerUOMPdfExport = moq && moqUOMPdfExport ? api.formatNumber("###,###.##", moq) + " / " + moqUOMPdfExport : null

    exportPdf.add(
            exportRow + [
                    "moq"          : moq?.toString() ?: "",
                    "moqUom"       : moqPerUOMPdfExport ?: "",
            ] as Map<String, Object>
    )

    summaryRows.add(
            row + [
                    (columnLabels."MOQPerUOM"): moqPerUOM,
            ] as Map<String, Object>
    )
}
api.local.exportPdf = exportPdf

def hasItemIncluded = summaryRows?.find { it[(columnLabels."ItemIncluded")] }
def hasLegacyPartNumber = summaryRows?.find { it[(columnLabels."LegacyPartNumber")] }

if (!hasItemIncluded) columnLabels.remove("ItemIncluded")
if (!hasLegacyPartNumber) columnLabels.remove("LegacyPartNumber")

def summary = api.newMatrix(columnLabels.collect({ column -> column.value }))

def rowToAdd
summaryRows.each {
    rowToAdd = it

    if (!hasItemIncluded) rowToAdd.remove(constants.ITEM_INCLUDED)
    if (!hasLegacyPartNumber) rowToAdd.remove(constants.LEGACY_PART_NUMBER_EA)

    summary.addRow(rowToAdd)
}

summary.setColumnFormat("MaterialNumber", FieldFormatType.TEXT)
summary.setColumnFormat("MaterialDescription", FieldFormatType.TEXT)
summary.setColumnFormat("MOQPerUOM", FieldFormatType.TEXT)
if (!hasItemIncluded) summary.setColumnFormat("ItemIncluded", FieldFormatType.TEXT)
if (!hasLegacyPartNumber) summary.setColumnFormat("LegacyPartNumber", FieldFormatType.TEXT)

summary.withEnableClientFilter(true)

warnings.unique()
warnings.each {
    api.addWarning(it)
}

api.local.hasItemIncluded = hasItemIncluded
api.local.hasLegacyPartNumber = hasLegacyPartNumber

return summary

def getFormattedPrice(price, numberOfDecimals, per = null) {
    if(!price) return null
    if(per != 1) return price
    switch (numberOfDecimals){
        case 2:
            return api.formatNumber("#####0.00", price)
        case 3:
            return api.formatNumber("#####0.000", price)
        case 4:
            return api.formatNumber("#####0.0000", price)
        default:
            return api.formatNumber("#####0.00", price)
    }
}

String formatPrice(BigDecimal price) {
    if (!price) return price
    def parts = String.format('%.2f', price).split('\\.')
    def integerPart = parts[0].reverse().replaceAll(/(\d{3})(?=\d)/, '$1,').reverse()
    return "\$ ${integerPart}.${parts[1]}"
}

List mapZBPLCRScales (crScales) {
    def scale
    return crScales?.split("\\|")?.collect {
        scale = it?.split("=")
        return [
                ScaleQty: scale[0]?.toBigDecimal() ?: null,
                Price   : scale[1]?.toBigDecimal() ?: null
        ]
    } ?: []
}

Map getDefaultFirstScale (moq, moqUOM, quotePrice, pricingUOM) {
    return [
            ScaleQty        : moq,
            ScaleUOM        : moqUOM,
            Price           : quotePrice,
            PriceUOM        : pricingUOM
    ]
}

List getDefaultValidScales (moq, moqUOM, quotePrice, pricingUOM) {
    return [
            [
                    ScaleQty        : moq,
                    ScaleUOM        : moqUOM,
                    Price           : quotePrice,
                    PriceUOM        : pricingUOM,
            ]
    ]
}