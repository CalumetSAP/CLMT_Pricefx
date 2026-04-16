if (api.isInputGenerationExecution()) return

def quote = api.currentItem()
if (quote?.get("quoteType") != "ExistingContractUpdate") return

final constants = libs.QuoteConstantsLibrary.General
final calculations = libs.QuoteLibrary.Calculations
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def lineItems = api.currentItem("lineItems")
String user = api.user("loginName")
def creationUser = quote?.createdByName

def stepNumber, stepLabel, quoteNumber, quoteName, quoteTypedId
if (quote != null) {
    stepNumber = quote.creationWorkflowCurrentStep
    stepLabel = quote.creationWorkflowStepLabel
    quoteNumber = quote.uniqueName
    quoteName = quote.label
    quoteTypedId = quote.typedId
}

def steps = []

def approversAlerts = []
def hasGuardrailError, hasApproverError
lineItems.each {
    hasGuardrailError = calculations.getInputValue(it, lineItemConstants.GUARDRAIL_ERROR_HIDDEN_ID)
    if (hasGuardrailError) approversAlerts.add(hasGuardrailError?.Error)
}

if (approversAlerts) api.throwException(approversAlerts.join(", "))

def alerts = []
def alertsAux = []
def thirdParty, namedPlace, customerMaterial
lineItems.each {
    alertsAux = []
    thirdParty = getInputByName(it?.inputs, lineItemConstants.THIRD_PARTY_CUSTOMER_ID)
    namedPlace = getInputByName(it?.inputs, lineItemConstants.NAMED_PLACE_ID)
    customerMaterial = getInputByName(it?.inputs, lineItemConstants.CUSTOMER_MATERIAL_NUMBER_ID)
    if (thirdParty?.size() > 35) alertsAux.add("3rd Party Customer Support is too long (max. 35)")
    if (namedPlace?.size() > 25) alertsAux.add("Named Place is too long (max. 25)")
    if (customerMaterial?.size() > 35) alertsAux.add("Customer Material # is too long (max. 35)")
    if (alertsAux) alerts.add(it?.sku + " - " + alertsAux.join(", "))
}

if (alerts) api.throwException(alerts.join(", "))

def hasFreightValue = lineItems.find { calculations.getInputValue(it, lineItemConstants.FREIGHT_ESTIMATE_ID) }

if (hasFreightValue) {
    def step1 = api.newCreationWorkflowStep()
            .withLabel(constants.USER_GROUP_FREIGHT)
            .withUserGroupAssignee(constants.USER_GROUP_FREIGHT)
            .withShouldSendNotification(false)
    steps.add(step1)

    def step2 = api.newCreationWorkflowStep()
            .withLabel(constants.USER_GROUP_PRICING)
            .withUserGroupAssignee(constants.USER_GROUP_PRICING)
            .withShouldSendNotification(false)
    steps.add(step2)
} else {

    def step1 = api.newCreationWorkflowStep()
            .withLabel(constants.USER_GROUP_FREIGHT)
            .withUserGroupAssignee(constants.USER_GROUP_FREIGHT)
            .withShouldSendNotification(false)
    steps.add(step1)

    def step2 = api.newCreationWorkflowStep()
            .withLabel(constants.USER_GROUP_PRICING)
            .withUserGroupAssignee(constants.USER_GROUP_PRICING)
            .withShouldSendNotification(false)
    steps.add(step2)
}

def creationWF = api.newCreationWorkflow().withSteps(*steps)

if (!hasFreightValue) creationWF.resetCurrentStepTo(1)

if ((stepNumber == 0 && stepLabel == null) || (stepNumber == 1 && (stepLabel == constants.USER_GROUP_PRICING))) {
    def isStepGroup = api.isUserInGroup(constants.USER_GROUP_PRICING, user)
    def userGroup = "Pricing"
    def step = stepLabel ? "Finish CW" : "Start CW"

    if (!isStepGroup) api.throwException("Only users with '${userGroup}' user group can ${step}")

    if (hasFreightValue && stepLabel == null) sendEmail(getEmail(constants.CREATION_WORKFLOW_STEP_2_GROUP, creationUser), quoteNumber, quoteName, quoteTypedId, user)

    def requiredInputs = []
    requiredInputs.addAll(lineItemConstants.REQUIRED_PRICING_INPUTS)
    requiredInputs.remove(lineItemConstants.SHIP_TO_ID)
    requiredInputs.remove(lineItemConstants.PLANT_ID)
    requiredInputs.removeAll(lineItemConstants.REQUIRED_FREIGHT_ESTIMATE_INPUTS)
    requiredInputs.unique()

    def dropdown = getDropdownOptionsValues()

    def division
    for (lineItem in lineItems) {
        if (lineItem.folder) continue

        if (calculations.getInputValue(lineItem, lineItemConstants.REJECTION_REASON_ID)) continue

        def lineRequiredInputs = []
        def freightRequiredInputs = []
        lineRequiredInputs.addAll(requiredInputs)

        def dsData = calculations.getInputValue(lineItem, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
        division = dsData?.get("Division")

        def configurator = calculations.getInputValue(lineItem, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)
        def priceType = findCode(calculations.getInputValue(lineItem, lineItemConstants.PRICE_TYPE_ID), "PriceType", dropdown)

        if (priceType == "1") lineRequiredInputs.addAll(lineItemConstants.REQUIRED_PRICE_TYPE_INPUTS_1)
        if (priceType == "2") {
            lineRequiredInputs.addAll(lineItemConstants.REQUIRED_PRICE_TYPE_INPUTS_2)
            lineRequiredInputs.remove(lineItemConstants.PRICE_LIST_ID)
            lineRequiredInputs.add("RecommendedPrice")
        }
        if (priceType == "3") lineRequiredInputs.addAll(lineItemConstants.REQUIRED_PRICE_TYPE_INPUTS_3)
        if (priceType == "4") lineRequiredInputs.removeAll(lineItemConstants.NOT_REQUIRED_PRICE_TYPE_INPUTS_4)

        if (calculations.getInputValue(lineItem, lineItemConstants.INCO_TERM_ID) == "FCA") lineRequiredInputs.removeAll(lineItemConstants.REQUIRED_DISTINCT_FCA_INPUTS + lineItemConstants.REQUIRED_FREIGHT_ESTIMATE_INPUTS)
        if (calculations.getInputValue(lineItem, lineItemConstants.FREIGHT_ESTIMATE_ID)) {
            freightRequiredInputs.addAll(lineItemConstants.REQUIRED_FREIGHT_ESTIMATE_INPUTS)
            freightRequiredInputs.remove(lineItemConstants.FREIGHT_AMOUNT_ID)
            freightRequiredInputs.remove(lineItemConstants.FREIGHT_UOM_ID)
            freightRequiredInputs.remove(lineItemConstants.FREIGHT_VALID_FROM_ID)
            freightRequiredInputs.remove(lineItemConstants.FREIGHT_VALID_TO_ID)
        }

        def found = lineRequiredInputs.findAll {
            if (it == lineItemConstants.PF_CONFIGURATOR_ADDER_ID) {
                return calculations.getInputValue(lineItem, it) == null
            } else if (it == "RecommendedPrice") {
                return !getOutputByName(lineItem?.outputs, it)
            } else {
                return !calculations.getInputValue(lineItem, it)
            }
        }
        def foundFreight = freightRequiredInputs.findAll { !configurator?.get(it) }
        if (found || foundFreight) {
            def labels = found.join(" ") + foundFreight.join(" ")
            api.throwException("${lineItem?.sku} - Required fields are missing " + labels)
        }
    }
}

if (stepNumber == 1 && stepLabel == constants.USER_GROUP_FREIGHT) {
    def isSecondStepGroup = api.isUserInGroup(constants.CREATION_WORKFLOW_STEP_2_GROUP, user)

    if (!isSecondStepGroup) api.throwException("Only users with 'Freight' user group can modify the quote")

    def userGroup = constants.USER_GROUP_PRICING
    def dsData = calculations.getInputValue(lineItems?.find(), lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
    def division = dsData?.get("Division")
    sendEmail(getEmail(userGroup, creationUser, division), quoteNumber, quoteName, quoteTypedId, user)

    def requiredInputs = lineItemConstants.REQUIRED_FREIGHT_INPUTS

    for (lineItem in lineItems) {
        if (lineItem.folder) continue

        if (calculations.getInputValue(lineItem, lineItemConstants.REJECTION_REASON_ID)) continue

        def configurator = calculations.getInputValue(lineItem, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)

        if (!calculations.getInputValue(lineItem, lineItemConstants.FREIGHT_ESTIMATE_ID)) continue

        def found = requiredInputs.findAll { !configurator?.get(it) }
        if (found) {
            def labels = found.join(" ")
            api.throwException("${lineItem?.sku} - Required fields are missing " + labels)
        }
    }
}

return creationWF

def getInputByName(inputs, name) {
    return inputs?.find { it.name == name }?.value
}

def getOutputByName(outputs, name) {
    return outputs?.find { it.resultName == name}?.result
}

def getEmail(step, creationUser, division = null) {
    if (step == libs.QuoteConstantsLibrary.General.USER_GROUP_SALES) return libs.QuoteLibrary.Query.findEmailByUser(creationUser)
    def filters = []
    filters.add(Filter.equal("key1", step))
    if (division) filters.add(Filter.equal("key2", division))
    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.WORKFLOW_EMAILS, ["attribute1"], null, *filters)?.find()?.attribute1
}

def sendEmail(to, quoteNumber, quoteName, quoteTypedId, user) {
    def subject = "Pricefx - Creation Workflow Step Assigned - Quote: " + quoteNumber + " - " + quoteName
    def link = api.getBaseURL() + "/pricefx/" + api.currentPartitionName() + "/saml/signon/?RelayState=Partition--targetPage=quotes--targetPageState=" + quoteTypedId
    def message = """<html>
					<body>
						<h1>${quoteNumber} - ${quoteName}</h1>
						<p>Quote creation workflow step assigned to you by: ${user}</p>
                        <table>
                          <tbody>
                              <tr style="padding:0;text-align:left;vertical-align:top">
                                <th class="small-12 large-4 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:20px;padding-right:10px;text-align:left;width:180px">
                                  <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                      <th style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
                                        <table class="button radius large expanded" style="Margin:0 0 16px 0;border-collapse:collapse;border-radius:3px;border-spacing:0;margin:0 0 16px 0;margin-bottom:8px;padding:0;text-align:left;vertical-align:top;width:100%!important">
                                          <tr style="padding:0;text-align:left;vertical-align:top">
                                            <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                              <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                  <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;background:#0070D2;border:none;border-collapse:collapse!important;border-radius:3px;color:#fefefe;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                                    <center data-parsed="" style="min-width:0;width:100%"><a href="${link}" align="center" class="float-center" style="Margin:0;border:0 solid #0070D2;border-radius:3px;color:#fefefe;display:inline-block;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:700;line-height:1.3;margin:0;padding:15px 25px 15px 25px;padding-left:0;padding-right:0;text-align:center;text-decoration:none;width:100%">View Quote</a></center>
                                                  </td>
                                                </tr>
                                              </table>
                                            </td>
                                           <td class="expander" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0!important;text-align:left;vertical-align:top;visibility:hidden;width:0;word-wrap:break-word"></td>
                                           </tr>
                                         </table>
                                       </th>
                                     </tr>
                                   </table>
                                 </th>
                              </tr>
                          </tbody>
                        </table>
					</body>
				 </html>"""


    api.sendEmail(to, subject, message)
}

def findCode(value, key, dropdownOptions) {
    return value
            ? dropdownOptions?.get(key)?.find { k, v -> v.toString().toUpperCase().startsWith(value.toUpperCase() as String) }?.key
            : value
}

def getDropdownOptionsValues() {
    def tablesConstants = libs.QuoteConstantsLibrary.Tables

    def filters = [
            Filter.equal("key1", "Quote"),
            Filter.in("key2", ["FreightTerm", "PriceType"]),
    ]
    def data = api.findLookupTableValues(tablesConstants.DROPDOWN_OPTIONS, *filters)

    data.inject([:]) { formatted, entry ->
        String key = entry["key2"]
        def value = [(entry["key3"]) : (entry["attribute1"])]
        formatted[key] = formatted.containsKey(key) ? formatted[key] + value : value
        formatted
    } ?: [:]
}