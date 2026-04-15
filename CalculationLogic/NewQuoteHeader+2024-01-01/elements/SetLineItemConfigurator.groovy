import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final dateUtils = libs.QuoteLibrary.DateUtils
final general = libs.QuoteConstantsLibrary.General

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def soldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID)

def soldToIndustry = out.FindSoldToIndustry && !api.isInputGenerationExecution() ? out.FindSoldToIndustry : null
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def incotermOptions = out.FindIncoTerm && !api.isInputGenerationExecution() ? out.FindIncoTerm as Map : [:]
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as Map : [:]
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as Map : [:]
def pricelistOptions = out.FindPricelist && !api.isInputGenerationExecution() ? out.FindPricelist as Map : [:]
def productMasterData = out.FindProductMasterData && !api.isInputGenerationExecution() ? out.FindProductMasterData as Map : [:]
def shipToOptions = out.FindCustomerShipTo && !api.isInputGenerationExecution() ? out.FindCustomerShipTo as List : []
def shippingPointNames = out.FindShippingPoint && !api.isInputGenerationExecution() ? out.FindShippingPoint as Map : [:]
def salesPersonOptions = api.local.salesPersonTable && !api.isInputGenerationExecution() ? api.local.salesPersonTable as List : []
//def indexNumberOptions = out.FindIndexValues && !api.isInputGenerationExecution() ? out.FindIndexValues as List : []
def globalUOMConversionTable = out.FindGlobalUOMConversionTable && !api.isInputGenerationExecution() ? out.FindGlobalUOMConversionTable as Map : [:]
def uomConversionTable = out.FindUOMConversionTable && !api.isInputGenerationExecution() ? out.FindUOMConversionTable as Map : [:]
def costPX = out.FindCostPX && !api.isInputGenerationExecution() ? out.FindCostPX as Map : [:]
def guardrails = api.local.guardrailsTable && !api.isInputGenerationExecution() ? api.local.guardrailsTable as Map : [:]
def packageDifferential = out.FindPackageDifferential && !api.isInputGenerationExecution() ? out.FindPackageDifferential as Map : [:]
def approversMap = out.FindApprovers && !api.isInputGenerationExecution() ? out.FindApprovers as Map : [:]
def exclusions = out.FindExclusions && !api.isInputGenerationExecution() ? out.FindExclusions as Map : [:]
def c4cUOM = out.FindC4CUOM && !api.isInputGenerationExecution() ? out.FindC4CUOM as Map : [:]

def uomOptions = api.local.uomOptions && !api.isInputGenerationExecution() ? api.local.uomOptions as Map : [:]

def headerEffectiveDate = headerConfigurator?.get(headerConstants.CONTRACT_EFFECTIVE_DATE_ID)
def defaultValidFromDate = headerEffectiveDate ? dateUtils.parseToDate(headerEffectiveDate) : dateUtils.getToday()

def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceValidTo"]?.values()?.find() as Integer : null
def defaultValidToDate = dateUtils.sumDays(defaultValidFromDate, days)

def creator = quoteProcessor?.getQuoteView()?.createdByName
def salesCreator = creator && api.isUserInGroup(general.USER_GROUP_SALES, creator) ? creator : null

def params = [
        Division                    : division,
        SalesOrg                    : salesOrg,
        IsSoldToOnly                : soldToOnly,
        SoldToIndustry              : soldToIndustry,
        DropdownOptions             : dropdownOptions,
        IncotermOptions             : incotermOptions,
        MeansOfTransportationOptions: meansOfTransportationOptions,
        ModeOfTransportationOptions : modeOfTransportationOptions,
        Pricelists                  : pricelistOptions,
        ValidFromDate               : defaultValidFromDate,
        ValidToDate                 : defaultValidToDate,
        ShipTo                      : shipToOptions,
        ShippingPointNames          : shippingPointNames,
        SalesPerson                 : salesPersonOptions,
        GlobalUOMTable              : globalUOMConversionTable,
        UOMTable                    : uomConversionTable,
        CostPX                      : costPX,
        Guardrails                  : guardrails,
        PackageDifferential         : packageDifferential,
        ApproversMap                : approversMap,
        SalesCreator                : salesCreator,
        Exclusions                  : exclusions,
        C4CUOM                      : c4cUOM,
]

if (!api.isInputGenerationExecution() && out.FindCustomerShipTo?.size() == 1) {
    params.putAll([
            (lineItemConstants.SHIP_TO_ID)         : out.FindCustomerShipTo?.find(),
            (lineItemConstants.SHIP_TO_INDUSTRY_ID): api.global.shipToData?.Industry,
            (lineItemConstants.SHIP_TO_ADDRESS_ID) : api.global.shipToData?.Address,
            (lineItemConstants.SHIP_TO_CITY_ID)    : api.global.shipToData?.City,
            (lineItemConstants.SHIP_TO_STATE_ID)   : api.global.shipToData?.State,
            (lineItemConstants.SHIP_TO_ZIP_ID)     : api.global.shipToData?.Zip,
            (lineItemConstants.SHIP_TO_COUNTRY_ID) : api.global.shipToData?.Country,
    ])
}


for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

//    def uoms = ["Flat Rate"]
    def uoms = []
    def uomsPerMaterial = uomOptions?.getOrDefault(lnProduct.sku, [])
    uomsPerMaterial?.add("KG")
    uomsPerMaterial?.sort()
    uomsPerMaterial?.unique()
    uoms.addAll(uomsPerMaterial)

    def conditionalParams = [
            Product           : productMasterData?.get(lnProduct?.sku),
            Plant             : out.FindPlants && !api.isInputGenerationExecution() ? out.FindPlants?.getOrDefault(lnProduct.sku, [])?.sort() as List : [],
            PricingAndSalesUOM: uomsPerMaterial,
            FreightUOM        : uoms,
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.NEW_QUOTE_CONFIGURATOR_LABEL,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.NEW_QUOTE_CONFIGURATOR_URL,
            "readOnly": false,
            "value"   : previousValues + params + conditionalParams,
    ])
}

return null
