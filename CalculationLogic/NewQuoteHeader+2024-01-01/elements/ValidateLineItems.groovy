if (!quoteProcessor.isPostPhase()) return

String commandExecution = api.currentContext().commandName
if (!["calculate", "submit", "testexec", "creationworkflowsubmit"].contains(commandExecution)) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
final general = libs.QuoteConstantsLibrary.General

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def salesOrg = headerConfigurator?.get(headerConstants.SALES_ORG_ID)
def isSoldToOnly = headerConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID) as Boolean

def productMasterData = out.FindProductMasterData ?: [:]
def duplicatedLines = api.local.duplicatedLines ?: [:]

if (api.local.divisionError && commandExecution != "submit") api.yellowAlert("The following selected Materials are in a different division than the selected customer: " + api.local.divisionError.join(", "))

def alerts = []
def duplicatedLinesAlerts = []

def inputMap = []
def inputMapApprover = []

def shipTo, material, thirdPartyCustomer, plant, plantCode, meansOfTransportation, modeOfTransportation, incoterm
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    def recommendedValues = calculations.getInputValue(lnProduct, lineItemConstants.GUARDRAIL_VALUES_HIDDEN_ID)

    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)
    def industry = isSoldToOnly ? out.FindIndustryList : configurator?.get(lineItemConstants.SHIP_TO_INDUSTRY_ID)
    def ph = getPH(productMasterData?.get(lnProduct.sku))
    plant = configurator?.get(lineItemConstants.PLANT_ID)
    def map = [:]

    if (recommendedValues?.Error) {
        map = [
                "Error"    : lnProduct.sku + " - " + recommendedValues?.Error,
                "EmailText": calculations.noGuardrailFoundText(lnProduct.sku, industry, ph, plant),
                "Group"    : general.USER_GROUP_NEW_RECOMMENDED_PRICE,
                "Subject"  : "No Guardrail Exists"
        ]
        updateInputValue(lnProduct.lineId, lineItemConstants.GUARDRAIL_ERROR_HIDDEN_ID, map)
        if (recommendedValues?.Error == "No Guardrail Exists") inputMap.add(map)
    } else {
        updateInputValue(lnProduct.lineId, lineItemConstants.GUARDRAIL_ERROR_HIDDEN_ID, null)
    }

    if (recommendedValues?.ApproverError) {
        map = [
                "Error"    : lnProduct.sku + " - " + recommendedValues?.ApproverError?.ErrorMessage,
                "EmailText": calculations.noApproverFoundText(division, salesOrg, industry, ph, recommendedValues?.ApproverError?.ApprovalLevel),
                "Group"    : general.USER_GROUP_APPROVER_ADMIN,
                "Subject"  : "Approver is missing"
        ]
        updateInputValue(lnProduct.lineId, lineItemConstants.APPROVER_ERROR_HIDDEN_ID, map)
        inputMapApprover.add(map)
        alerts.add(lnProduct.sku + " - " + map?.Error)
    } else {
        updateInputValue(lnProduct.lineId, lineItemConstants.APPROVER_ERROR_HIDDEN_ID, null)
    }

    if (recommendedValues?.Error  == "No Conversion Factor found for Guardrail") alerts.add(lnProduct.sku + " - " + recommendedValues?.Error)

    if (commandExecution == "submit") {
        if (!configurator?.get(lineItemConstants.FREIGHT_ESTIMATE_ID)) continue

        def freightRequiredInputs = []
        freightRequiredInputs.addAll(lineItemConstants.REQUIRED_FREIGHT_INPUTS)
        def foundFreight = freightRequiredInputs.findAll { !configurator?.get(it) }

        if (foundFreight) api.throwException("Freight inputs are missing. Click the \"Back\" button to resubmit to Freight.")
    }

    shipTo = out.FindCustomerShipTo?.size() == 1 ? out.FindCustomerShipTo?.find()?.split(" - ")?.getAt(0) : configurator?.get(lineItemConstants.SHIP_TO_ID)?.split(" - ")?.getAt(0)
    if (!shipTo) shipTo = ""
    material = lnProduct.sku
    thirdPartyCustomer = configurator?.get(lineItemConstants.THIRD_PARTY_CUSTOMER_ID) ?: ""
    plantCode = configurator?.get(lineItemConstants.PLANT_ID)?.split(" - ")?.getAt(0) ?: ""
    meansOfTransportation = configurator?.get(lineItemConstants.MEANS_OF_TRANSPORTATION_ID) ?: ""
    modeOfTransportation = configurator?.get(lineItemConstants.MODE_OF_TRANSPORTATION_ID) ?: ""
    incoterm = configurator?.get(lineItemConstants.INCO_TERM_ID) ?: ""


    def key = shipTo + "|" + material + "|" + thirdPartyCustomer + "|" + plantCode + "|" + meansOfTransportation + "|" + modeOfTransportation + "|" + incoterm
    def duplicatedLine = duplicatedLines?.get(key)

    if (duplicatedLine) {
        duplicatedLinesAlerts.add(lnProduct.sku + " - " + "This may be a duplicate of line " + duplicatedLine.SAPLineID + " on contract number " + duplicatedLine.SAPContractNumber)
    }

}

if (commandExecution == "creationworkflowsubmit" && inputMap) {
    def body = [
            "data": [
                    "errorList": inputMap
            ]
    ]
    def response = api.boundCall("SystemUpdate", "/formulamanager.executeformula/SendEmailLogic", api.jsonEncode(body).toString(), false)
}

if (commandExecution == "submit" && inputMapApprover) {
    def body = [
            "data": [
                    "errorList": inputMapApprover
            ]
    ]
    def response = api.boundCall("SystemUpdate", "/formulamanager.executeformula/SendEmailLogic", api.jsonEncode(body).toString(), false)
    api.throwException(alerts.join(", "))
}

if (duplicatedLinesAlerts) api.yellowAlert(duplicatedLinesAlerts.join(", "))
if (alerts) api.yellowAlert(alerts.join(", "))

return

def updateInputValue(String lineId, name, defaultValue) {
    quoteProcessor.addOrUpdateInput(
            lineId, [
            "name"        : name,
            "value"       : defaultValue,
    ])
}

def getPH(productMasterItem) {
    if (productMasterItem?.PH4Code) return productMasterItem?.PH4Code
    if (productMasterItem?.PH3Code) return productMasterItem?.PH3Code
    if (productMasterItem?.PH2Code) return productMasterItem?.PH2Code
    return productMasterItem?.PH1Code
}