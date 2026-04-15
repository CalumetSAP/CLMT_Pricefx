import groovy.transform.Field
import net.pricefx.common.apibuilder.quote.QuoteBuilder

@Field final String PENDING_STATUS = "PENDING"
@Field final String PROCESSING_STATUS = "PROCESSING"
@Field final String PENDING_SAP_QUOTES = "PENDING SAP QUOTES"
@Field final String PROCESSING_SAP_QUOTES = "PROCESSING SAP QUOTES"
@Field final String WAITING_SAP_UPDATE = "WAITING SAP UPDATE"
@Field final String RE_WAITING_SAP_UPDATE = "RE-WAITING SAP UPDATE"
@Field final String PROCESSING_SAP_UPDATE = "PROCESSING SAP UPDATE"
@Field final String PENDING_CONDITION_RECORD = "PENDING CONDITION RECORD"
@Field final String PENDING_CUSTOM_EVENT = "PENDING CUSTOM EVENT"
@Field final String READY_STATUS = "READY"
@Field final String ERROR_STATUS = "ERROR"
@Field final String EMPTY = "Empty"

def addOrUpdateStatusToPending(String quoteId, String quoteType) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    setPendingStatus(cptData, quoteId, quoteType)
}
def addOrUpdateStatusToProcessing(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setProcessingStatus(cptData, quoteId)
    }
}
def addOrUpdateStatusToPendingSAPQuotes(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setPendingSAPQuotesStatus(cptData, quoteId)
    }
}
def addOrUpdateStatusToProcessingSAPQuotes(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setProcessingSAPQuotesStatus(cptData, quoteId)
    }
}
def addOrUpdateStatusToWaitingSAPUpdateQuotes(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setWaitingSAPUpdateStatus(cptData, quoteId)
    }
}
def addOrUpdateStatusToReWaitingSAPUpdateQuotes(quoteId) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    setReWaitingSAPUpdateStatus(cptData, quoteId)
}
def addOrUpdateStatusToProcessingSAPUpdateQuotes(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setProcessingSAPUpdateStatus(cptData, quoteId)
    }
}
def addOrUpdateStatusToPendingConditionRecord(quoteId) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    setPendingConditionRecordStatus(cptData, quoteId)
}
def addOrUpdateStatusToReady(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setReadyStatus(cptData, quoteId)
    }
}
def addOrUpdateStatusToPendingCustomEvent(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setPendingCustomEventStatus(cptData, quoteId)
    }
}

def addOrUpdateStatusToError(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS)
    quoteIds.each { quoteId ->
        setErrorStatus(cptData, quoteId)
    }
}

def addOrUpdateQuoteForScalesStatusToPending(String quoteId) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS_FOR_SCALES)
    setPendingStatus(cptData, quoteId)
}
def addOrUpdateQuoteForScalesStatusToProcessing(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS_FOR_SCALES)
    quoteIds.each { quoteId ->
        setProcessingStatus(cptData, quoteId)
    }
}
def addOrUpdateQuoteForScalesStatusToReady(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS_FOR_SCALES)
    quoteIds.each { quoteId ->
        setReadyStatus(cptData, quoteId)
    }
}
def addOrUpdateQuoteForScalesStatusToError(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS_FOR_SCALES)
    quoteIds.each { quoteId ->
        setErrorStatus(cptData, quoteId)
    }
}

def addLineIDsForCRStatusToPendingUsingBoundCall(List lineIDs) {
    def cptId = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.LINE_IDS_FOR_CR_STATUS)?.id
    if (cptId) {
        def pendingBody = [
                "data": [
                        "attribute1": PENDING_STATUS
                ],
                "operation": "add"
        ]
        for (lineID in lineIDs) {
            pendingBody.data.name = lineID
            api.boundCall("SystemUpdate", "/lookuptablemanager.add/${cptId.toString()}", api.jsonEncode(pendingBody).toString(), false)
        }
    }
}
def addQuotesForCRStatusToPending(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTES_FOR_CR_STATUS)
    quoteIds.each { quoteId ->
        setPendingStatus(cptData, quoteId)
    }
}
def addOrUpdateLineIDsForCRStatusToReady(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.LINE_IDS_FOR_CR_STATUS)
    quoteIds.each { quoteId ->
        setReadyStatus(cptData, quoteId)
    }
}
def addOrUpdateQuotesForCRStatusToReady(List quoteIds) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.QUOTES_FOR_CR_STATUS)
    quoteIds.each { quoteId ->
        setReadyStatus(cptData, quoteId)
    }
}

private def setPendingStatus (cptData, quoteId, quoteType = null) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PENDING_STATUS, quoteType))
}

private def setProcessingStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PROCESSING_STATUS))
}

private def setPendingSAPQuotesStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PENDING_SAP_QUOTES))
}
private def setProcessingSAPQuotesStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PROCESSING_SAP_QUOTES))
}
private def setWaitingSAPUpdateStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, WAITING_SAP_UPDATE))
}
private def setReWaitingSAPUpdateStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, RE_WAITING_SAP_UPDATE))
}
private def setProcessingSAPUpdateStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PROCESSING_SAP_UPDATE))
}
private def setPendingConditionRecordStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PENDING_CONDITION_RECORD))
}
private def setReadyStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, READY_STATUS))
}
private def setPendingCustomEventStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, PENDING_CUSTOM_EVENT))
}
private def setErrorStatus (cptData, quoteId) {
    api.addOrUpdate("MLTV", buildRowToAddOrUpdate(cptData, quoteId, ERROR_STATUS))
}

private def buildRowToAddOrUpdate (cptData, quoteId, status, quoteType = null) {
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "name"              : quoteId,
            "attribute1"        : status,
    ]
    if (quoteType) {
        ppRow.attribute2 = quoteType
    }

    return ppRow
}

def getPendingLineIDsForCRIDs () {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.LINE_IDS_FOR_CR_STATUS, "lastUpdateDate", Filter.equal("attribute1", PENDING_STATUS))?.collect { it.name }
}
def getPendingExistingQuotesForCRIDs() {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.QUOTES_FOR_CR_STATUS, "lastUpdateDate", Filter.equal("attribute1", PENDING_STATUS))?.collect { it.name }
}
def getPendingExistingQuotesForCRIDs(Filter extraFilter) {
    def filter = Filter.and(
            Filter.equal("attribute1", PENDING_STATUS),
            extraFilter
    )
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.QUOTES_FOR_CR_STATUS, "lastUpdateDate", filter)?.collect { it.name }
}
def getPendingSAPQuotesRows () {
    return getCPTRowsByCPTNameAndOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, PENDING_SAP_QUOTES)
}
def getProcessingSAPQuotesRows () {
    return getCPTRowsByCPTNameAndOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, PROCESSING_SAP_QUOTES)
}
def getProcessingSAPUpdateRows () {
    return getCPTRowsByCPTNameAndOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, PROCESSING_SAP_UPDATE)
}
def getWaitingOrReWaitingSAPUpdateRows () {
    return getCPTRowsByCPTNameAndMoreThanOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, [WAITING_SAP_UPDATE, RE_WAITING_SAP_UPDATE])
}
def getPendingConditionRecordRows () {
    return getCPTRowsByCPTNameAndOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, PENDING_CONDITION_RECORD)
}
def getPendingCustomEventRows (Filter addedFilters = null) {
    def filters = []
    filters.add(Filter.equal("attribute1", PENDING_CUSTOM_EVENT))
    if (addedFilters) filters.add(addedFilters)

    return getCPTRowsByCPTNameAndFilter(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, Filter.and(*filters))
}

def getPendingQuotesRows () {
    return getCPTRowsByCPTNameAndOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, PENDING_STATUS)
}
def getProcessingQuotesRows () {
    return getCPTRowsByCPTNameAndOneStatus(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS, PROCESSING_STATUS)
}

private def getCPTRowsByCPTNameAndOneStatus (String cptName, String status) {
    return getCPTRowsByCPTNameAndFilter(cptName, Filter.equal("attribute1", status))
}
private def getCPTRowsByCPTNameAndMoreThanOneStatus (String cptName, List<String> status) {
    return getCPTRowsByCPTNameAndFilter(cptName, Filter.in("attribute1", status))
}

private def getCPTRowsByCPTNameAndFilter (String cptName, filter) {
    return api.findLookupTableValues(cptName, "lastUpdateDate", filter)?.collect {
        [
                quoteId     : it.name,
                quoteType   : it.attribute2
        ]
    }
}

def getQuoteIdsForScalesByStatus(String status) {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.QUOTE_STATUS_FOR_SCALES, "lastUpdateDate", Filter.equal("attribute1", status))
            ?.collect {
                it.name
            }
}

def calculateLineItemCost(BigDecimal standardPrice, BigDecimal costingLotSize, BigDecimal units) {
    if (standardPrice == null || !costingLotSize || units == null) return null

//    return libs.QuoteLibrary.RoundingUtils.round((standardPrice / costingLotSize) * units, 2)
    return (standardPrice / costingLotSize) * units
}

def getInputValue(lineItem, name) {
    return lineItem?.inputs?.find { it.name == name}?.value
}

def getHiddenInputValue(lineItem, name) {
    return lineItem?.inputs?.find { it.name == (name + "Hidden") }?.value
}

def getPH1(productHierarchy) {
    def digits = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH1
    if (productHierarchy?.size() < digits) return

    return productHierarchy?.take(digits)
}

def getPH2(productHierarchy) {
    def digits = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH2
    def startIndex = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH1
    if (productHierarchy?.size() < startIndex + digits) return

    return productHierarchy.substring(startIndex).take(digits)
}

def getPH3(productHierarchy) {
    def digits = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH3
    def startIndex = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH1 + libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH2
    if (productHierarchy?.size() < startIndex + digits) return

    return productHierarchy.substring(startIndex).take(digits)
}

def getPH4(productHierarchy) {
    def digits = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH4
    def startIndex = libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH1 + libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH2 + libs.QuoteConstantsLibrary.General.NUMBER_OF_DIGITS_FOR_PH3
    if (productHierarchy?.size() < startIndex + digits) return

    return productHierarchy.substring(startIndex).take(digits)
}

def convertValue(value, numerator, denominator) {
    if (!value || !numerator || !denominator) return value
    return (value * numerator) / denominator
}

def groupGuardrailData(data) {
    def guardrailMap = [:]
    data?.each {
        if (!guardrailMap[it.key2]) {
            guardrailMap[it.key2] = [:]
        }
        if (!guardrailMap[it.key2][it.key1]) {
            guardrailMap[it.key2][it.key1] = [:]
        }
        if (!guardrailMap[it.key2][it.key1][it.key3]) {
            guardrailMap[it.key2][it.key1][it.key3] = [:]
        }
        guardrailMap[it.key2][it.key1][it.key3][it.key4] = it
    }
    return guardrailMap
}

def calculateGuardrailValues(guardrailMap, modeOfTransportation, industry, material, phs, price, uomConversionMap, pricingUOM) {
    int i = 0
    def guardrailValues
    while (i < phs?.size() && !guardrailValues) {
        guardrailValues = getGuardrailValue(guardrailMap, modeOfTransportation, industry, material, phs?.get(i))
        i++
    }
    if (!guardrailValues) return [:]

    def guardrailPrice = guardrailValues.attribute1
    if (guardrailValues.attribute6 != pricingUOM) {
        def uomConversionValues = uomConversionMap?.get(material + "|" + pricingUOM)
        guardrailPrice = convertValue(guardrailPrice, uomConversionValues?.Numerator, uomConversionValues?.Denominator)
    }

    def approvalLevels = getApprovalLevels(price, guardrailPrice, 2, guardrailValues.attribute3, guardrailValues.attribute4, guardrailValues.attribute5)

    return [
            GuardrailPrice: guardrailPrice,
            ApprovalLevels: approvalLevels
    ]
}

def getGuardrailValue(guardrailMap, modeOfTransportation, industry, material, ph) {
    def firstMap = guardrailMap[modeOfTransportation]
    if (!firstMap) return null

    def result = findExactGuardrailValueOrByPH(firstMap, industry, material, ph)
    if (result) return result

    result = findExactGuardrailValueOrByPH(firstMap, industry, "*", ph)
    if (result) return result

    result = findExactGuardrailValueOrByPH(firstMap, "*", material, ph)
    if (result) return result

    result = findExactGuardrailValueOrByPH(firstMap, "*", "*", ph)
    if (result) return result

    return null
}

def findExactGuardrailValueOrByPH(map, firstKey, secondKey, ph) {
    def result = map?.get(firstKey)?.get(secondKey)

    if (!result) return null
    if (result.size() == 1) return result?.values()?.find()

    return result?.get(ph)
}

def getApprovalLevels(price, guardrailPrice, numberOfDecimals, pricingApprovalLevel1, pricingApprovalLevel2, pricingApprovalLevel3) {
    if (!price || !guardrailPrice) return 0

    def roundedPrice = libs.QuoteLibrary.RoundingUtils.round(price, numberOfDecimals?.toInteger())
    if (roundedPrice >= guardrailPrice) return 0

    def discount = BigDecimal.ONE - (roundedPrice?.toBigDecimal() / guardrailPrice?.toBigDecimal())
    discount = discount.abs() * 100

    if (pricingApprovalLevel3?.toBigDecimal() != BigDecimal.ZERO && discount > pricingApprovalLevel3?.toBigDecimal()) return 3
    if (pricingApprovalLevel2?.toBigDecimal() != BigDecimal.ZERO && discount > pricingApprovalLevel2?.toBigDecimal()) return 2
    if (pricingApprovalLevel1 != null && discount > pricingApprovalLevel1?.toBigDecimal()) return 1
    return 0
}

def groupApproversData(data) {
    def approversMap = [:]
    data?.each {
        if (!approversMap[it.key5]) {
            approversMap[it.key5] = [:]
        }
        if (it.key6 != "*") {
            approversMap[it.key5][it.key6] = it.attribute3
        } else {
            if (!approversMap[it.key5][it.key4]) {
                approversMap[it.key5][it.key4] = [:]
            }
            approversMap[it.key5][it.key4][it.key3] = it.attribute3
        }
    }
    return approversMap
}

def groupTotalApproversData(data) {
    def approversMap = [:]
    data?.each {
        if (!approversMap[it.key2]) {
            approversMap[it.key2] = [:]
        }
        if (!approversMap[it.key2][it.key1]) {
            approversMap[it.key2][it.key1] = [:]
        }
        if (!approversMap[it.key2][it.key1][it.key5]) {
            approversMap[it.key2][it.key1][it.key5] = [:]
        }
        if (it.key6 != "*") {
            approversMap[it.key2][it.key1][it.key5][it.key6] = it.attribute3
        } else {
            if (!approversMap[it.key2][it.key1][it.key5][it.key4]) {
                approversMap[it.key2][it.key1][it.key5][it.key4] = [:]
            }
            approversMap[it.key2][it.key1][it.key5][it.key4][it.key3] = it.attribute3
        }
    }
    return approversMap
}

def getApproverValue(approversMap, level, salesPerson, industry, ph4, ph3, ph2, ph1) {
    def firstMap = approversMap?.get(level)
    if (!firstMap) return null

    def result = firstMap?.get(salesPerson)
    if (result) return result

    result = findApproverByIndustryOrEmpty(firstMap, industry, ph4)
    if (result) return result

    result = findApproverByIndustryOrEmpty(firstMap, industry, ph3)
    if (result) return result

    result = findApproverByIndustryOrEmpty(firstMap, industry, ph2)
    if (result) return result

    result = findApproverByIndustryOrEmpty(firstMap, industry, ph1)
    if (result) return result

    result = findApproverByIndustryOrEmpty(firstMap, industry, "*")
    if (result) return result

    return null
}

def findApproverByIndustryOrEmpty(approversMap, industry, ph) {
    def result = approversMap?.get(ph)?.get(industry)
    if (result) return result
    return approversMap?.get(ph)?.get("*")
}

def getApprovedAndSubmissionDate(uniqueName) {
    def workflow = api.find("W", Filter.equal("approvableUniqueName", uniqueName))

    def approvalDate = null
    def submissionDate = null
    if (workflow?.lastUpdateDate) approvalDate = (workflow?.lastUpdateDate[0]).format("yyyy-MM-dd HH:mm:ss")
    if (workflow?.createDate) submissionDate = (workflow?.createDate[0]).format("yyyy-MM-dd HH:mm:ss")

    return [
            ApprovalDate  : approvalDate,
            SubmissionDate: submissionDate
    ]
}

def getFormulaApproverValue(approversMap, salesPerson, industry, ph2, ph1) {
    def result = approversMap?.get(salesPerson)
    if (result) return result

    result = approversMap?.get(ph2)?.get(industry)
    if (result) return result

    result = approversMap?.get(ph1)?.get(industry)
    if (result) return result

    result = approversMap?.get(ph2)?.get("*")
    if (result) return result

    result = approversMap?.get(ph1)?.get("*")
    if (result) return result

    return null
}

def isFreightGroupStep(commandName, currentStep, stepLabel, user) {
    if (commandName == "calculate") return currentStep == 1 && stepLabel == libs.QuoteConstantsLibrary.General.CREATION_WORKFLOW_STEP_2_LABEL
    if (commandName == "creationworkflowsubmit") {
        return (currentStep == 0 && stepLabel == libs.QuoteConstantsLibrary.General.CREATION_WORKFLOW_STEP_1_LABEL) ||
                (currentStep == 2 && stepLabel == libs.QuoteConstantsLibrary.General.CREATION_WORKFLOW_STEP_1_LABEL) ||
                (currentStep == null && stepLabel == null && api.isUserInGroup(libs.QuoteConstantsLibrary.General.CREATION_WORKFLOW_STEP_1_GROUP, user))
    }
    return false
}

def removePlantDescription(plants) {
    def plantsWithoutNull = plants?.findAll()
    if (!plantsWithoutNull) return []
    return plantsWithoutNull?.collect { it.split(" ")?.getAt(0) }
}

def getExistingLines(QuoteBuilder quoteBuilder) {
    def lineItemConstants = libs.QuoteConstantsLibrary.LineItem
    def calculations = libs.QuoteLibrary.Calculations

    def existingLines = []
    for (lnProduct in quoteBuilder.getQuoteView().lineItems) {
        if (lnProduct.folder) continue
        existingLines.add([
                SAPContract: calculations.getInputValue(lnProduct, lineItemConstants.SAP_CONTRACT_ID),
                LineNumber : calculations.getInputValue(lnProduct, lineItemConstants.LINE_NUMBER_ID),
        ])
    }

    return existingLines
}

def groupExclusionsData(data) {
    def exclusionsMap = [:]
    data?.each {
        if (!exclusionsMap[it.key2]) {
            exclusionsMap[it.key2] = [:]
        }
        if (!exclusionsMap[it.key2][it.key1]) {
            exclusionsMap[it.key2][it.key1] = [:]
        }
        if (!exclusionsMap[it.key2][it.key1][it.key3]) {
            exclusionsMap[it.key2][it.key1][it.key3] = [:]
        }
        exclusionsMap[it.key2][it.key1][it.key3][it.key4] = it
    }
    return exclusionsMap
}

def getExclusionsValue(exclusionsMap, soldTo, businessUnit, shipTo, material) {
    def firstMap = exclusionsMap[soldTo]
    if (!firstMap) return null

    def result = getByKeys(firstMap, businessUnit, shipTo, material)
    if (result) return result

    result = getByKeys(firstMap, businessUnit, "*", material)
    if (result) return result

    result = getByKeys(firstMap, businessUnit, shipTo, "*")
    if (result) return result

    result = getByKeys(firstMap, businessUnit, "*", "*")
    if (result) return result

    result = getByKeys(firstMap, "*", "*", "*")
    if (result) return result

    return null
}

def getByKeys(map, firstKey, secondKey, thirdKey) {
    return map?.get(firstKey)?.get(secondKey)?.get(thirdKey)
}

def getPriceProtectionDataByExclusion(exclusionsMap, soldTo, businessUnit, shipTo, material) {
    def exclusion = getExclusionsValue(exclusionsMap, soldTo, businessUnit, shipTo, material)
    if (!exclusion) return null

    def priceProtection = exclusion.attribute15 == "A" ? "1" : (exclusion.attribute15 == "E" ? "2" : "3")
    def numberOfDays = priceProtection == "1" || priceProtection == "2" ? exclusion.attribute1 : null
    def movementTiming = priceProtection == "3" ? (exclusion.attribute2 && exclusion.attribute3 && exclusion.attribute4 ? "Month" : "Quarter") : null

    def auxMovementStart =  movementTiming == "Quarter" ? (exclusion.attribute2 ?: (exclusion.attribute3 ?: exclusion.attribute4)) : null
    def movementStart = auxMovementStart ? (auxMovementStart?.split("/")?.size() >= 0 ? auxMovementStart?.split("/")?.getAt(0) : null) : null

    def auxMovementDay = priceProtection == "3" ? (movementTiming == "Quarter" ? auxMovementStart : exclusion.attribute2) : null
    def movementDay = auxMovementDay?.split("/")?.size() > 0 ? auxMovementDay?.split("/")?.getAt(1) : null

    return [
            PriceProtection: priceProtection,
            NumberOfDays   : numberOfDays,
            MovementTiming : movementTiming,
            MovementStart  : movementStart,
            MovementDay    : movementDay,
    ]
}

def calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelistType, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku) {
    def guardrailValues = getGuardrailsValue(guardrailMap, industry, plant, sku, phs, pricelistType)
    if (!guardrailValues?.attribute1) return [Error: "No Guardrail Exists"]

    def guardrailKey = guardrailValues.key1 + "-" + guardrailValues.key2 + "-" + guardrailValues.key3 + "-" + guardrailValues.key4 + "-" + guardrailValues.key5

    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, guardrailValues.attribute2, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
    def recommendedPrice = conversionFactor != null ? guardrailValues.attribute1 * conversionFactor : null
    def error
    if (conversionFactor == null) error = "No Conversion Factor found for Guardrail"
    def approvalLevels = getApprovalLevels(price, recommendedPrice, 2, guardrailValues.attribute3, guardrailValues.attribute4, guardrailValues.attribute5)

    return [
            RecommendedPrice: recommendedPrice,
            ApprovalLevels  : approvalLevels,
            GuardrailKey    : guardrailKey,
    ]
}

def calculateGuardrailsValues(guardrailMap, industry, plant, material, phs, pricelist, price, globalUOMConversionMap, uomConversionMap, pricingUOM, sku,
                              priceType, basePricing, basePricingUOM, packageDifferentialMap, approversMap, product, numberOfDecimals, salesPerson) {
    if (priceType == "3" && !basePricing) return [Error: "No valid record found in the Pricing table"]

    def recommendedPrice, guardrailKey, guardrailValues, guardrailPrice, onlyPLKey

    if (priceType != "3") {
        guardrailValues = getGuardrailsValue(guardrailMap, industry, plant, sku, phs, pricelist)
        onlyPLKey = guardrailValues?.key1 == "*" && guardrailValues?.key2 == "*" && guardrailValues?.key3 == "*" && guardrailValues?.key4 == "*" &&
                guardrailValues?.key5 != "*"
    }

    def isPricelistType = priceType == "3" || (priceType == "2" && onlyPLKey)

    if (!isPricelistType || priceType == "2") {
        guardrailValues = getGuardrailsValue(guardrailMap, industry, plant, sku, phs, pricelist)
        if (!isPricelistType) {
            if (!guardrailValues?.attribute1) return [Error: "No Guardrail Exists"]
            guardrailPrice = guardrailValues?.attribute1
            guardrailKey = guardrailValues?.key1 + "-" + guardrailValues?.key2 + "-" + guardrailValues?.key3 + "-" + guardrailValues?.key4 + "-" + guardrailValues?.key5
        }
    }

    if (isPricelistType) {
        guardrailPrice = basePricing
        guardrailKey = "PL " + pricelist
    }

    def guardrailUOM = isPricelistType ? basePricingUOM : guardrailValues?.attribute2

    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, guardrailUOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
    if (conversionFactor == null) return [Error           : "No Conversion Factor found for Guardrail. From " + guardrailUOM + " to " + pricingUOM,
                                          GuardrailKey    : guardrailKey]
    recommendedPrice = guardrailPrice * conversionFactor

    if (!isPricelistType && guardrailValues?.key3?.size() != 12) {
        def containerCode = sku.size() > 6 ? sku.substring(sku.size() - 3, sku.size()) : sku
        def packageDifferential = getValueFromKeyWithPhs(packageDifferentialMap, containerCode + "|", "", phs) ?: [:]

        conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, packageDifferential?.UOM, pricingUOM, uomConversionMap, globalUOMConversionMap)?.toBigDecimal()
        def convertedPackageDifferential = conversionFactor != null && packageDifferential?.Value ? packageDifferential?.Value * conversionFactor : BigDecimal.ZERO

        recommendedPrice = recommendedPrice ? recommendedPrice + convertedPackageDifferential : null
    }

    def discountApprover, approverError, approvalLevels
    if (priceType == "2") {
        def gridMessage = "Invalid Approver in Approvers Table"

        approvalLevels = getApprovalLevels(price, recommendedPrice, numberOfDecimals, guardrailValues?.attribute3, guardrailValues?.attribute4, guardrailValues?.attribute5)

        discountApprover = getApproverValue(approversMap, approvalLevels?.toString(), salesPerson, industry, product?.PH4Code, product?.PH3Code, product?.PH2Code, product?.PH1Code)
        if (!discountApprover) gridMessage = "No Approver"
        discountApprover = approvalLevels != 0 ? libs.QuoteLibrary.Query.findFullNamesByUser([discountApprover])?.find() : null

        approverError = recommendedPrice && !discountApprover && approvalLevels != 0
                ? [ ErrorMessage : "Approver is missing for Approval Sequence: ${guardrailKey}",
                    ApprovalLevel: approvalLevels,
                    GridMessage  : gridMessage]
                : null
    }

    return [
            RecommendedPrice   : libs.QuoteLibrary.RoundingUtils.round(recommendedPrice, numberOfDecimals?.toInteger()),
            GuardrailKey       : guardrailKey,
            DiscountApprover   : discountApprover,
            ApproverError      : approverError,
            ApprovalLevel      : approvalLevels?.toString()
    ]
}

def noGuardrailOrApproverFound(group, subject, text) {
    def filter = Filter.and(
            Filter.isNotNull("groups"),
            Filter.notEqual("groups", "")
    )
    def userEmails = api.find("U", 0, api.getMaxFindResultsLimit(), null, null, filter)
            .findAll { it.groups
                    .find { it.uniqueName == group}
            }?.email?.unique()

    userEmails?.each { email ->
        if (email) {
            api.sendEmail(email as String, subject, text)
        }
    }

    return null
}

def noGuardrailFoundText(material, industry, ph, plant) {
    return "No guardrail exists for " +
            "Material: ${material} " +
            "Industry: ${industry} " +
            "Product Hierarchy: ${ph} " +
            "Plant: ${plant}"
}

def noApproverFoundText(division, salesOrg, industry, ph, approvalLevel) {
    return "Approver is missing for " +
            "Division: ${division} " +
            "Sales Organization: ${salesOrg} " +
            "Industry ${industry} " +
            "Product Hierarchy: ${ph} " +
            "Approval Level: ${approvalLevel}"
}

def getGuardrailsValue(guardrailMap, industry, plant, material, phs, pricelistType) {
    def mats = materialPrefixes(material)
    def result, key

    // A
    result = lookupWithMaterials(guardrailMap, mats) { m -> industry + "|" + plant + "|" + m + "|*|*" }
    if (result) return result

    // B
    result = lookupWithMaterials(guardrailMap, mats) { m -> industry + "|*|" + m + "|*|*" }
    if (result) return result

    // C
    result = lookupWithMaterials(guardrailMap, mats) { m -> "*|" + plant + "|" + m + "|*|*" }
    if (result) return result

    // D
    result = lookupWithMaterials(guardrailMap, mats) { m -> "*|*|" + m + "|*|*" }
    if (result) return result

    // E
    result = getValueFromKeyWithPhs(guardrailMap, industry + "|" + plant + "|*|", "|*", phs)
    if (result) return result

    // F
    result = getValueFromKeyWithPhs(guardrailMap, industry + "|*|*|", "|*", phs)
    if (result) return result

    // G
    result = getValueFromKeyWithPhs(guardrailMap, "*|" + plant + "|*|", "|*", phs)
    if (result) return result

    // H
    result = lookupWithMaterials(guardrailMap, mats) { m -> industry + "|*|" + m + "|*|" + pricelistType }
    if (result) return result

    // I
    key = industry + "|*|*|*|" + pricelistType
    result = guardrailMap?.get(key)
    if (result) return result

    // J
    result = lookupWithMaterials(guardrailMap, mats) { m -> "*|*|" + m + "|*|" + pricelistType }
    if (result) return result

    // K
    result = getValueFromKeyWithPhs(guardrailMap, "*|*|*|", "|" + pricelistType, phs)
    if (result) return result

    // L
    key = "*|*|*|*|" + pricelistType
    result = guardrailMap?.get(key)
    if (result) return result

    // M
    result = getValueFromKeyWithPhs(guardrailMap, "*|*|*|", "|*", phs)
    if (result) return result

    return null
}

List<String> materialPrefixes(String material) {
    if (!material) return []
    int max = Math.min(material.size(), 12)
    int min = Math.min(6, max)
    (max..min).collect { material.substring(0, it) }
}

def lookupWithMaterials(Map guardrailMap, List<String> mats, Closure<String> keyBuilder) {
    for (m in mats) {
        def k = keyBuilder(m)
        def v = guardrailMap?.get(k)
        if (v) return v
    }
    return null
}

def getValueFromKeyWithPhs(map, firstPartKey, secondPartKey, phs) {
    int i = 0
    def value, key
    while (i < phs?.size() && !value) {
        key = firstPartKey + phs?.get(i) + secondPartKey
        value = map?.get(key)
        i++
    }
    return value
}

def addContractUUID (id, contractOrKey = null) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID)
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "key1"              : id?.toString(),
            "key2"              : contractOrKey ?: EMPTY,
            "attribute1"        : api.uuid()
    ]

    api.addOrUpdate("MLTV2", ppRow)
}

def addContractsUUIDs (contractsUUIDs) {
    def cptData = api.findLookupTable(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID)
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName
    ]
    for (contractUUID in contractsUUIDs) {
        ppRow.key1 = contractUUID.UpdatedbyID
        ppRow.key2 = contractUUID.SAPContractNumber
        ppRow.attribute1 = contractUUID.uuid

        api.addOrUpdate("MLTV2", ppRow)
    }
}

String getNewQuoteUUID (id) {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID, ["attribute1"], null, Filter.equal("key1", id))?.find()?.attribute1
}

List<String> getNewQuoteUUIDs (id) {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID, ["attribute1"], null, Filter.equal("key1", id))?.attribute1
}

Map getContractsUUIDs (id) {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID, ["key2", "attribute1"], null, Filter.equal("key1", id))?.collectEntries {
        [(it.key2): it.attribute1]
    } ?: [:]
}

List<String> getUUIDsById (id) {
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.CONTRACT_UUID, ["key2", "attribute1"], null, Filter.equal("key1", id))
}

def findBasePricing(key, moq, basePricingMap, scales) {
    def pricingData = basePricingMap?.get(key)
    if (!pricingData) return

    def basePrice = pricingData.BasePrice
    def condRecNo = pricingData.ConditionRecordNo

    def scalesData = scales?.get(condRecNo) as List
    if (!scalesData) return basePrice

    def conditionRate = scalesData?.findAll { it.ScaleQuantity <= moq }?.max { it.ScaleQuantity }?.ConditionRate
    if (!conditionRate) return basePrice

    return conditionRate
}

def findBasePricingNew(key, moq, moqUOM, basePricingMap, scales, scaleUOM, sku, uomConversionMap, globalUOMConversionMap) {
    def pricingData = basePricingMap?.get(key)
    if (!pricingData) return

    def basePrice = pricingData.BasePrice

    def scalesData = getScalesData(pricingData, scales)
    if (!scalesData) return basePrice

    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, scaleUOM, moqUOM, uomConversionMap, globalUOMConversionMap) ?: 1
    def conditionRate = scalesData?.findAll { (it.ScaleQuantity * conversionFactor) <= moq }?.max { it.ScaleQuantity }?.ConditionRate
    if (!conditionRate) return basePrice

    return conditionRate
}

def findBasePricingLineItem(moq, moqUOM, pricingData, scalesData, scaleUOM, sku, uomConversionMap, globalUOMConversionMap) {
    if (!pricingData) return
    def basePrice = pricingData.BasePrice

    if (!scalesData) return basePrice

    def conversionFactor = libs.QuoteLibrary.Conversion.getConversionFactor(sku, scaleUOM, moqUOM, uomConversionMap, globalUOMConversionMap) ?: 1
    def conditionRate = scalesData?.findAll { (it.ScaleQuantity * conversionFactor) <= moq }?.max { it.ScaleQuantity }?.ConditionRate
    if (!conditionRate) return basePrice

    return conditionRate
}

def getScalesData(pricingData, scales) {
    if (pricingData?.ConditionRecordNo) {
        return scales?.get(pricingData?.ConditionRecordNo) as List
    } else if (pricingData?.Scales) {
        return pricingData?.Scales?.toString()?.split("\\|")?.collect { item ->
            def parts = item.split("=")
            [
                    ScaleQuantity: parts[0].toBigDecimal(),
                    ConditionRate: parts[1].toBigDecimal()
            ]
        }
    } else if (pricingData?.QuoteID && pricingData?.LineID) {
        return scales?.get(pricingData?.QuoteID + "|" + pricingData?.LineID) as List
    } else {
        return []
    }
}