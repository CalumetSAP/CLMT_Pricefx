if (!quoteProcessor.isPrePhase() || !out.CalculateRecommendedPrice) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final roundingUtils = libs.QuoteLibrary.RoundingUtils
final query = libs.QuoteLibrary.Query

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def isSoldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID) as Boolean

def productMasterData = out.FindProductMasterData ?: [:]
def approversMap = out.FindApprovers ?: [:]
def recommendedValuesMap = out.CalculateRecommendedPrice ?: [:]

def guardrailMap = [:]
def approverMap = [:]
def formulaApproverMap = [:]
def approvalSequenceMap = [:]

def configurator, productMasterItem, numberOfDecimals, priceType, industry, recommendedValues, approver, formulaApprover, salesPerson
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String

    productMasterItem = productMasterData?.get(lnProduct.sku)

    configurator = calculations.getInputValue(lnProduct, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)

    numberOfDecimals = configurator?.get(lineItemConstants.NUMBER_OF_DECIMALS_ID) ?: "2"
    recommendedValues = recommendedValuesMap?.get(lineId)

    approver = recommendedValues?.DiscountApprover
    updateInputValue(lineId, lineItemConstants.APPROVER_HIDDEN_ID, approver?.LoginName, null)
    updateInputValue(lineId, lineItemConstants.APPROVER_LEVEL_HIDDEN_ID, recommendedValues?.ApprovalLevel, null)
    updateInputValue(lineId, lineItemConstants.GUARDRAIL_VALUES_HIDDEN_ID, recommendedValues, null)

//    if (recommendedValues?.Error) {
//
//    } else {
//
//    }
    def guardrailPrice = roundingUtils.round(recommendedValues?.RecommendedPrice?.toBigDecimal(), numberOfDecimals?.toInteger())?.toString()
    guardrailMap.put(lnProduct.lineId, guardrailPrice)

    def approverOutput = approver?.FullName
    if (recommendedValues?.ApproverError) {
        approverOutput = recommendedValues?.ApproverError?.GridMessage
        updateInputValue(lineId, lineItemConstants.APPROVER_HIDDEN_ID, approverOutput, null)
    }

    approverMap.put(lnProduct.lineId, approverOutput)
    approvalSequenceMap.put(lnProduct.lineId, recommendedValues?.GuardrailKey)

    industry = isSoldToOnly ? out.FindIndustryList : configurator?.get(lineItemConstants.SHIP_TO_INDUSTRY_ID)
    priceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)
    salesPerson = configurator?.get(lineItemConstants.SALES_PERSON_ID)
    salesPerson = salesPerson?.contains(" - ") ? salesPerson?.split(" - ")[0] : salesPerson
    formulaApprover = null
    def findApproverInTable
    def formulaApproverOutput
    if (priceType == "1") {
        findApproverInTable = calculations.getFormulaApproverValue(approversMap["2"], salesPerson, industry, productMasterItem?.PH2Code, productMasterItem?.PH1Code)
        formulaApprover = query.findFullNamesByUser([findApproverInTable])?.find()

        def ph = productMasterItem?.PH2Code ?: productMasterItem?.PH1Code
        if (!formulaApprover) {
            updateInputValue(lnProduct.lineId, lineItemConstants.APPROVER_ERROR_HIDDEN_ID, [
                    "Error"    : lnProduct.sku + " - Formula Approver is missing",
                    "EmailText": calculations.noApproverFoundText(division, salesOrg, industry, ph, "2")
            ], null)
            def errorMessage = findApproverInTable ? "Invalid Approver in Approvers Table" : "No Approver"
            formulaApproverOutput = errorMessage
            updateInputValue(lineId, lineItemConstants.FORMULA_APPROVER_HIDDEN_ID, errorMessage, null)
        } else {
            formulaApproverOutput = formulaApprover?.FullName
            updateInputValue(lnProduct.lineId, lineItemConstants.APPROVER_ERROR_HIDDEN_ID, null, null)
            updateInputValue(lineId, lineItemConstants.FORMULA_APPROVER_HIDDEN_ID, formulaApprover?.LoginName, null)
        }
        formulaApproverMap.put(lnProduct.lineId, formulaApproverOutput)
    } else {
        updateInputValue(lineId, lineItemConstants.FORMULA_APPROVER_HIDDEN_ID, null, null)
    }
}

api.global.guardrailMap = guardrailMap
api.global.approverMap = approverMap
api.global.formulaApproverMap = formulaApproverMap
api.global.approvalSequenceMap = approvalSequenceMap

return null

def updateInputValue(String lineId, name, defaultValue, previousValue) {
    if (previousValue) return
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}