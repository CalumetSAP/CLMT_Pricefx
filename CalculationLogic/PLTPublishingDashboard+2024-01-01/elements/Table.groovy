import net.pricefx.common.api.FieldFormatType

import java.text.SimpleDateFormat

def visited = new HashSet<>()
def constants = libs.DashboardConstantsLibrary.PLTDashboard

def showJobbers = out.Filters?.get(constants.SHOW_JOBBER_SRP_MAP_INPUT_KEY)

def products = api.global.products
def currencyDecimalsMap = api.global.currencyDecimals
def pricingData = api.global.pricingData
def uomDescription = api.global.uomDescription

def configurationBreakdownReference = api.global.configurationBreakdownReference

def warnings = []

def outputFormatDateTime = new SimpleDateFormat("MM/dd/yyyy")

def columnLabels = [
        MaterialNumber      : constants.MATERIAL_NUMBER,
        LegacyPartNumber    : constants.LEGACY_PART_NUMBER_EA,
        MaterialDescription : constants.MATERIAL_DESCRIPTION,
        ItemIncluded        : constants.ITEM_INCLUDED,
        EffectiveDate       : constants.EFFECTIVE_DATE,
        MOQPerUOM           : constants.MOQ_PER_UOM,
        Price               : constants.PRICE,
        UOM                 : constants.UOM,
        Jobbers             : constants.JOBBER_DEALER_PRICE,
        SRP                 : constants.SRP,
        MAP                 : constants.MAP,
]

def summaryRows = []

def exportPdf = []

def pricelists = api.global.ZBPLmerge
def row, exportRow, keys, value, material, materialDescription, itemIncluded, effectiveDate, moqPerUOM, price, priceUOM, jobbers, srp, map
def productPM, legacyPart, priceUomPdfExport, pricingItem, pricelist, itemIncludedList, scales, firstScale, validScales, scaleToMOQConversionFactor, moq, moqUOM, moqUOMPdfExport,
        scaleQty, scalePrice, per, currencyDecimals, scalesAux, scaleUOM, brand, productHierarchy1, productHierarchy2, productHierarchy3, productHierarchy4, moqPerUOMPdfExport,
        productHierarchy2Desc, productHierarchy3Desc
def currency
for (entry in pricelists.entrySet()) {
    row = [:]
    exportRow = [:]

    keys = entry.key
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

    effectiveDate = value?.ValidFrom ? outputFormatDateTime.format(value?.ValidFrom) : null

    itemIncludedList = configurationBreakdownReference?.get(material) ?: []
    itemIncluded = itemIncludedList?.join(" ")

    currencyDecimals = currencyDecimalsMap?.get(value?.ConditionCurrency) ?: 2
    if (!currency) currency = value?.ConditionCurrency

    pricelist = value?.Pricelist
    pricingItem = pricingData?.get(material + "|" + pricelist)
    jobbers = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.JobberPrice?.toBigDecimal(), currencyDecimals)
    srp = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.SRP?.toBigDecimal(), currencyDecimals)
    map = libs.QuoteLibrary.RoundingUtils.round(pricingItem?.MAP?.toBigDecimal(), currencyDecimals)

    (row[columnLabels."MaterialNumber"] = material)
    (row[columnLabels."LegacyPartNumber"] = legacyPart)
    (row[columnLabels."MaterialDescription"] = materialDescription)
    (row[columnLabels."ItemIncluded"] = itemIncluded)
    (row[columnLabels."EffectiveDate"] = effectiveDate)
    if (showJobbers) (row[columnLabels."Jobbers"] = jobbers)
    if (showJobbers) (row[columnLabels."SRP"] = srp)
    if (showJobbers) (row[columnLabels."MAP"] = map)

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
    if (showJobbers) (exportRow["jobbers"] = formatPrice(jobbers) ?: "")
    if (showJobbers) (exportRow["srp"] = formatPrice(srp) ?: "")
    if (showJobbers) (exportRow["map"] = formatPrice(map) ?: "")


    price = value?.Amount
    priceUOM = value?.UnitOfMeasure
    moq = pricingItem?.MOQ
    moqUOM = pricingItem?.MOQUOM
    per = value?.Per
    scales = value?.ConditionRecordNo ? api.global.ZBPLScales.get(value?.ConditionRecordNo) : (value?.Scales ? mapZBPLCRScales(value?.Scales) : null)
    if (scales) {
        scaleToMOQConversionFactor = moq ? libs.QuoteLibrary.Conversion.getConversionFactor(material, value?.ScaleUoM, moqUOM, api.global.uomConversion, api.global.globalUOMConversion, visited)?.toBigDecimal() : BigDecimal.ONE
        if (scaleToMOQConversionFactor == null) api.throwException("Missing conversion from ZBPL Scale UOM (${value?.ScaleUoM}) to MOQ UOM (${moqUOM}) for material ${material}")
        if (!moqUOM) moqUOM = value?.ScaleUoM
        if (!moq) moq = 1

        scalesAux = moq ? scales.findAll { moq >= it.ScaleQty * scaleToMOQConversionFactor } : scales
        firstScale = scalesAux.max { it.ScaleQty * scaleToMOQConversionFactor }
        scalesAux = moq ? scales.findAll { moq < it.ScaleQty * scaleToMOQConversionFactor } : scales
        validScales = scalesAux.collect {
            it.ScaleQty *= scaleToMOQConversionFactor
            it.ScaleUOM = moqUOM
            it
        } ?: []
        if (firstScale) {
            firstScale.ScaleQty = moq
            firstScale.ScaleUOM = moqUOM
            validScales.add(firstScale)
        } else {
            validScales.add(getDefaultFirstScale(moq, moqUOM, price, priceUOM))
        }
        validScales.sort { it.ScaleQty }
    } else {
        validScales = getDefaultValidScales(moq, moqUOM, price, priceUOM)
    }

    for (validScale in validScales) {
        scaleQty = validScale.ScaleQty
        scaleUOM = validScale.ScaleUOM
        scalePrice = per && validScale.Price ? validScale.Price / per : validScale.Price

        price = getFormattedPrice(scalePrice, currencyDecimals, per)
        priceUomPdfExport = uomDescription?.get(priceUOM) ? uomDescription?.get(priceUOM) : priceUOM

        moqPerUOM = scaleQty && scaleUOM ? api.formatNumber("###,###.##", scaleQty) + " / " + scaleUOM : null

        moqUOMPdfExport = uomDescription?.get(scaleUOM) ? uomDescription?.get(scaleUOM) : scaleUOM
        moqPerUOMPdfExport = scaleQty && moqUOMPdfExport ? api.formatNumber("###,###.##", scaleQty) + " / " + moqUOMPdfExport : null

        exportPdf.add(
                exportRow + [
                        "effectiveDate": effectiveDate ?: "",
                        "moq"          : scaleQty?.toString() ?: "",
                        "moqUom"       : moqPerUOMPdfExport ?: "",
                        "price"        : formatPrice(price?.toBigDecimal()) ?: "",
                        "priceUOM"     : priceUomPdfExport ?: ""
                ] as Map<String, Object>
        )

        summaryRows.add(
                row + [
                        (columnLabels."MOQPerUOM"): moqPerUOM,
                        (columnLabels."Price")    : price,
                        (columnLabels."UOM")      : priceUOM,
                ] as Map<String, Object>
        )

    }
}
api.local.exportPdf = exportPdf

def hasItemIncluded = summaryRows?.find { it[(columnLabels."ItemIncluded")] }
def hasLegacyPartNumber = summaryRows?.find { it[(columnLabels."LegacyPartNumber")] }
def hasJobbbers = summaryRows?.find { it[(columnLabels."Jobbers")] }
def hasSRP = summaryRows?.find { it[(columnLabels."SRP")] }
def hasMAP = summaryRows?.find { it[(columnLabels."MAP")] }

if (!hasItemIncluded) columnLabels.remove("ItemIncluded")
if (!hasLegacyPartNumber) columnLabels.remove("LegacyPartNumber")
if (!hasJobbbers) columnLabels.remove("Jobbers")
if (!hasSRP) columnLabels.remove("SRP")
if (!hasMAP) columnLabels.remove("MAP")

def summary = api.newMatrix(columnLabels.collect({ column -> column.value }))

def rowToAdd
summaryRows.each {
    rowToAdd = it

    if (!hasItemIncluded) rowToAdd.remove(constants.ITEM_INCLUDED)
    if (!hasLegacyPartNumber) rowToAdd.remove(constants.LEGACY_PART_NUMBER_EA)
    if (!hasJobbbers) rowToAdd.remove(constants.JOBBER_DEALER_PRICE)
    if (!hasSRP) rowToAdd.remove(constants.SRP)
    if (!hasMAP) rowToAdd.remove(constants.MAP)

    summary.addRow(rowToAdd)
}

summary.setColumnFormat("MaterialNumber", FieldFormatType.TEXT)
summary.setColumnFormat("MaterialDescription", FieldFormatType.TEXT)
summary.setColumnFormat("EffectiveDate", FieldFormatType.DATE)
summary.setColumnFormat("MOQPerUOM", FieldFormatType.TEXT)
summary.setColumnFormat("Price", FieldFormatType.NUMERIC_LONG)
summary.setColumnFormat("UOM", FieldFormatType.TEXT)
if (!hasItemIncluded) summary.setColumnFormat("ItemIncluded", FieldFormatType.TEXT)
if (!hasLegacyPartNumber) summary.setColumnFormat("LegacyPartNumber", FieldFormatType.TEXT)
if (!hasJobbbers) summary.setColumnFormat("Jobbers", FieldFormatType.TEXT)
if (!hasSRP) summary.setColumnFormat("SRP", FieldFormatType.TEXT)
if (!hasMAP) summary.setColumnFormat("MAP", FieldFormatType.TEXT)

summary.withEnableClientFilter(true)

warnings.unique()
warnings.each {
    api.addWarning(it)
}

api.local.currency = currency
api.local.hasItemIncluded = hasItemIncluded
api.local.hasLegacyPartNumber = hasLegacyPartNumber
api.local.hasJobbbers = hasJobbbers || hasSRP || hasMAP

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