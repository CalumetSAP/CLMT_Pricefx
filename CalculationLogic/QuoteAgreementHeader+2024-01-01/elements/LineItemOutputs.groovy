import groovy.transform.Field

if (!quoteProcessor.isPostPhase()) return

final createOutput = libs.BdpLib.QuoteOutput
final lineItemOutputsConstants = libs.QuoteConstantsLibrary.LineItemOutputs
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final query = libs.QuoteLibrary.Query

def customerMasterData = out.FindCustomerMasterDataForLineItems ?: [:]
def productMasterData = out.FindProducts ?: [:]
def lineItemCalculations = out.CalculateLineItems && !api.isInputGenerationExecution() ? out.CalculateLineItems as Map : [:]
def uomConversionMap = out.FindUOMConversionTable ?: [:]
def modeOfTransportationMap = out.FindModeOfTransportationGroup ?: [:]
def pricingMap = out.FindBasePricing ?: [:]
//def listPriceMap = out.FindListPrice ?: [:]
def zdgsMap = out.FindZDGS ?: [:]
def zdgsScalesMap = out.FindZDGSScales ?: [:]
def guardrailMap = out.FindGuardrails ?: [:]
def packageDifferentialMap = out.FindPackageDifferential ?: [:]
def approversMap = out.FindApprovers ?: [:]

def customerMasterItem, productMasterItem
def calculatedValues, guardrailValues

def costValue, packageDifferential, industry, plant, price, pricingUOM, phs, pricelistType, priceType, formulaApprover, approver, approvers, guardrailPrice

def outputs
if (api.isUserInGroup("Pricing", api.local.loginName) || api.isUserInGroup("Freight", api.local.loginName)) {
    outputs = api.local.pricingAndFreightOutputs ?: []
} else if (api.isUserInGroup("Sales", api.local.loginName)) {
    outputs = api.local.salesOutputs ?: []
}

for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    customerMasterItem = customerMasterData?.get(lineId)
    productMasterItem = productMasterData?.get(lnProduct.sku)
    calculatedValues = lineItemCalculations?.get(lineId)

    costValue = calculatedValues?.Cost
//    modeOfTransportation = calculations.getInputValue(lnProduct, lineItemConstants.MODE_OF_TRANSPORTATION_ID)
//    modeOfTransportationGroup = modeOfTransportationMap?.get(modeOfTransportation?.split(" - ")?.getAt(0)?.trim())
    industry = customerMasterItem?.Industry
    plant = calculations.getInputValue(lnProduct, lineItemConstants.PLANT_ID)
    price = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_ID)
    pricelistType = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_LIST_ID)?.split(" - ")?.getAt(0)
    priceType = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
    pricingUOM = calculations.getInputValue(lnProduct, lineItemConstants.PRICING_UOM_ID)

    phs = []
    if (productMasterItem?.PH4Code) phs.add(productMasterItem?.PH4Code)
    if (productMasterItem?.PH3Code) phs.add(productMasterItem?.PH3Code)
    if (productMasterItem?.PH2Code) phs.add(productMasterItem?.PH2Code)
    if (productMasterItem?.PH1Code) phs.add(productMasterItem?.PH1Code)

    formulaApprover = null
    if (priceType == "1") {
        formulaApprover = calculations.getFormulaApproverValue(approversMap["2"], industry, productMasterItem?.PH2Code, productMasterItem?.PH1Code)
        formulaApprover = query.findFullNamesByUser([formulaApprover])?.find()
    }

    def material = lnProduct.sku.size() > 6 ? lnProduct.sku.take(6) : lnProduct.sku
    guardrailValues = calculations.calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelistType, price, uomConversionMap, pricingUOM)
    def basePricing = pricingMap?.get(lnProduct.sku)?.BasePrice
    if (priceType == "3" && basePricing) {
        def zdgs = zdgsMap?.get(lnProduct.sku)
        def zdgsScales = zdgsScalesMap?.get(zdgs?.CondRecNo)
        def moq = calculations.getInputValue(lnProduct, lineItemConstants.MOQ_ID)

        zdgsScales = moq ? zdgsScales?.findAll { moq >= it.ScaleQuantity } : zdgsScales
        def conditionRate
        if (moq) {
            conditionRate = zdgsScales?.findAll { moq >= it.ScaleQuantity }?.max { it.ScaleQuantity }?.ConditionRate
        } else {
            conditionRate = zdgsScales?.min { it.ScaleQuantity }?.ConditionRate
        }
        guardrailPrice = conditionRate ? basePricing + conditionRate : basePricing
    } else {
        def containerCode = lnProduct.sku.size() > 6 ? lnProduct.sku.substring(lnProduct.sku.size() - 3, lnProduct.sku.size()) : lnProduct.sku
        packageDifferential = calculations.getValueFromKeyWithPhs(packageDifferentialMap, containerCode + "|", "", phs) ?: BigDecimal.ZERO

        guardrailPrice = guardrailValues.RecommendedPrice ? guardrailValues.RecommendedPrice + packageDifferential : null
    }

    approver = calculations.getApproverValue(approversMap, guardrailValues.ApprovalLevels?.toString(), industry, productMasterItem?.PH4Code, productMasterItem?.PH3Code, productMasterItem?.PH2Code, productMasterItem?.PH1Code)
    approvers = query.findFullNamesByUser([approver])

    updateInputValue(lineId, lineItemConstants.FORMULA_APPROVER_HIDDEN_ID, formulaApprover?.LoginName, null)
    updateInputValue(lineId, lineItemConstants.APPROVER_HIDDEN_ID, approvers?.find()?.LoginName, null)

    // Scales Indicator
    def scalesConfigurator = calculations.getInputValue(lnProduct, lineItemConstants.SCALES_CONFIGURATOR_NAME)
    def scalesIndicator = scalesConfigurator?.get(lineItemConstants.SCALES_ID)?.size()

    quoteProcessor.with {
        if (outputs?.contains(lineItemOutputsConstants.MATERIAL_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.MATERIAL_ID,
                    lineItemOutputsConstants.MATERIAL_LABEL,
                    null,
                    productMasterItem?.Material
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.PRODUCT_DESCRIPTION_ID)) {
            def output = createOutput.text(
                    lineItemOutputsConstants.PRODUCT_DESCRIPTION_ID,
                    lineItemOutputsConstants.PRODUCT_DESCRIPTION_LABEL,
                    null,
                    productMasterItem?.ProductDescription
            )
//            output.put("cssProperties", "background-color:#ff7152")

            quoteProcessor.addOrUpdateOutput(lineId, output)
        }

        if (outputs?.contains(lineItemOutputsConstants.UOM_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.UOM_ID,
                    lineItemOutputsConstants.UOM_LABEL,
                    null,
                    productMasterItem?.UOM
            ))
        }
//        def uomOP = createOutput.text(
//                lineItemOutputsConstants.UOM_ID,
//                lineItemOutputsConstants.UOM_LABEL,
//                null,
//                productMasterItem?.UOM
//        )
//        uomOP.put("userGroup", "Pricing")
//        quoteProcessor.addOrUpdateOutput(lineId, uomOP)

        if (outputs?.contains(lineItemOutputsConstants.COST_ID)){
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.money(
                    lineItemOutputsConstants.COST_ID,
                    lineItemOutputsConstants.COST_LABEL,
                    null,
                    costValue
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.FORMULA_APPROVER_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.FORMULA_APPROVER_ID,
                    lineItemOutputsConstants.FORMULA_APPROVER_LABEL,
                    null,
                    formulaApprover?.FullName
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.SCALES_INDICATOR_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.numeric(
                    lineItemOutputsConstants.SCALES_INDICATOR_ID,
                    lineItemOutputsConstants.SCALES_INDICATOR_LABEL,
                    null,
                    scalesIndicator
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.GUARDRAIL_PRICE_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.money(
                    lineItemOutputsConstants.GUARDRAIL_PRICE_ID,
                    lineItemOutputsConstants.GUARDRAIL_PRICE_LABEL,
                    null,
                    guardrailPrice
            ))
        }


        if (outputs?.contains(lineItemOutputsConstants.APPROVER_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.APPROVER_ID,
                    lineItemOutputsConstants.APPROVER_LABEL,
                    null,
                    approvers?.find()?.FullName
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.SHIP_TO_INDUSTRY_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.SHIP_TO_INDUSTRY_ID,
                    lineItemOutputsConstants.SHIP_TO_INDUSTRY_LABEL,
                    null,
                    industry
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.SHIP_TO_ADDRESS_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.SHIP_TO_ADDRESS_ID,
                    lineItemOutputsConstants.SHIP_TO_ADDRESS_LABEL,
                    null,
                    customerMasterItem?.Address
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.SHIP_TO_CITY_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.SHIP_TO_CITY_ID,
                    lineItemOutputsConstants.SHIP_TO_CITY_LABEL,
                    null,
                    customerMasterItem?.City
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.SHIP_TO_STATE_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.SHIP_TO_STATE_ID,
                    lineItemOutputsConstants.SHIP_TO_STATE_LABEL,
                    null,
                    customerMasterItem?.State
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.SHIP_TO_COUNTRY_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.SHIP_TO_COUNTRY_ID,
                    lineItemOutputsConstants.SHIP_TO_COUNTRY_LABEL,
                    null,
                    customerMasterItem?.Country
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.PH1_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.PH1_ID,
                    lineItemOutputsConstants.PH1_LABEL,
                    null,
                    productMasterItem?.PH1Code ? productMasterItem?.PH1Code + " - " + productMasterItem?.PH1Description : null
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.PH2_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.PH2_ID,
                    lineItemOutputsConstants.PH2_LABEL,
                    null,
                    productMasterItem?.PH2Code ? productMasterItem?.PH2Code + " - " + productMasterItem?.PH2Description : null
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.PH3_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.PH3_ID,
                    lineItemOutputsConstants.PH3_LABEL,
                    null,
                    productMasterItem?.PH3Code ? productMasterItem?.PH3Code + " - " + productMasterItem?.PH3Description : null
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.PH4_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.PH4_ID,
                    lineItemOutputsConstants.PH4_LABEL,
                    null,
                    productMasterItem?.PH4Code ? productMasterItem?.PH4Code + " - " + productMasterItem?.PH4Description : null
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.MATERIAL_PACKAGE_STYLE_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.MATERIAL_PACKAGE_STYLE_ID,
                    lineItemOutputsConstants.MATERIAL_PACKAGE_STYLE_LABEL,
                    null,
                    productMasterItem?.MaterialPackageStyle
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.MESSAGE_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.MESSAGE_ID,
                    lineItemOutputsConstants.MESSAGE_LABEL,
                    null,
                    null
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.ACCESS_SEQUENCE_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.ACCESS_SEQUENCE_ID,
                    lineItemOutputsConstants.ACCESS_SEQUENCE_LABEL,
                    null,
                    null
            ))
        }

        if (outputs?.contains(lineItemOutputsConstants.APPROVAL_SEQUENCE_ID)) {
            quoteProcessor.addOrUpdateOutput(lineId, createOutput.text(
                    lineItemOutputsConstants.APPROVAL_SEQUENCE_ID,
                    lineItemOutputsConstants.APPROVAL_SEQUENCE_LABEL,
                    null,
                    guardrailValues.GuardrailKey
            ))
        }
    }
}

return null

def updateInputValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}