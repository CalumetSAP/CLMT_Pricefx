import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final dateUtils = libs.QuoteLibrary.DateUtils
final general = libs.QuoteConstantsLibrary.General

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def soldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID)

def soldToIndustry = null
def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
def incotermOptions = out.FindIncoTerm && !api.isInputGenerationExecution() ? out.FindIncoTerm as Map : [:]
def meansOfTransportationOptions = out.FindMeansOfTransportation && !api.isInputGenerationExecution() ? out.FindMeansOfTransportation as Map : [:]
def modeOfTransportationOptions = out.FindModeOfTransportation && !api.isInputGenerationExecution() ? out.FindModeOfTransportation as Map : [:]
def pricelistOptions = out.FindPricelist && !api.isInputGenerationExecution() ? out.FindPricelist as Map : [:]
def productMasterData = out.FindProductMasterData && !api.isInputGenerationExecution() ? out.FindProductMasterData as Map : [:]
def customerMasterData = out.FindCustomerShipTo && !api.isInputGenerationExecution() ? out.FindCustomerShipTo as Map : [:]
def shippingPointNames = out.FindShippingPoint && !api.isInputGenerationExecution() ? out.FindShippingPoint as Map : [:]
def salesPersonOptions = api.local.salesPersonTable && !api.isInputGenerationExecution() ? api.local.salesPersonTable as List : []
def globalUOMConversionTable = out.FindGlobalUOMConversionTable && !api.isInputGenerationExecution() ? out.FindGlobalUOMConversionTable as Map : [:]
//def uomConversionTable = out.FindUOMConversionTable && !api.isInputGenerationExecution() ? out.FindUOMConversionTable as Map : [:]
//def costPX = out.FindCostPX && !api.isInputGenerationExecution() ? out.FindCostPX as Map : [:]
//def guardrails = api.local.guardrailsTable && !api.isInputGenerationExecution() ? api.local.guardrailsTable as Map : [:]
def packageDifferential = out.FindPackageDifferential && !api.isInputGenerationExecution() ? out.FindPackageDifferential as Map : [:]
def approversMap = out.FindApprovers && !api.isInputGenerationExecution() ? out.FindApprovers as Map : [:]
def exclusions = out.FindExclusions && !api.isInputGenerationExecution() ? out.FindExclusions as Map : [:]

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
        ShipTo                      : customerMasterData,
        ValidFromDate               : defaultValidFromDate,
        ValidToDate                 : defaultValidToDate,
        ShippingPointNames          : shippingPointNames,
        SalesPerson                 : salesPersonOptions,
        GlobalUOMTable              : globalUOMConversionTable,
//        UOMTable                    : uomConversionTable,
//        CostPX                      : costPX,
//        Guardrails                  : guardrails,
        PackageDifferential         : packageDifferential,
        SalesCreator                : salesCreator,
        Exclusions                  : exclusions
]

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue

    def dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    def filteredApproversMap = approversMap?.get(dsData.Division ?: "")?.get(dsData.SalesOrg ?: "")

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
            ContractData      : dsData,
            ApproversMap      : filteredApproversMap,
    ]

    def previousValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
            "name"    : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
            "label"   : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME,
            "type"    : InputType.CONFIGURATOR,
            "url"     : lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_URL,
            "readOnly": false,
            "value"   : previousValues + params + conditionalParams,
    ])
}

return null