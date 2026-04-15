import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations

def indexNumberOptions = out.FindIndexValues && !api.isInputGenerationExecution() ? out.FindIndexValues as List : []
def referencePeriodOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["ReferencePeriod"] as Map : [:]
def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]
def numberOfDecimalsOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["NumberOfDecimals"] as Map : [:]
def recalculationPeriodOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["RecalculationPeriod"] as Map : [:]

def params = [
        IndexNumber        : indexNumberOptions,
        ReferencePeriod    : referencePeriodOptions,
        DecimalPlaces      : numberOfDecimalsOptions,
        RecalculationPeriod: recalculationPeriodOptions
]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def readOnly = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID) != "1"

    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()

    def conditionalParams = [
            ReadOnly: readOnly,
            sku     : lnProduct?.sku,
            PriceUOM: uomsPerMaterial,
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_NAME,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.PRICING_FORMULA_CONFIGURATOR_URL,
            "value"   : previousValues + params + conditionalParams,
    ])
}

return null
