if (api.isInputGenerationExecution()) return

def quote = api.currentItem()
if (quote?.get("quoteType") != "New Contract" && quote?.get("quoteType") != "NewContract") return

final constants = libs.QuoteConstantsLibrary.General
final calculations = libs.QuoteLibrary.Calculations
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def lineItems = api.currentItem("lineItems")
String user = api.user("loginName")
def creationUser = quote?.createdByName

def quoteNumber, quoteName, quoteTypedId
def stepNumber = api.currentItem('creationWorkflowCurrentStep')
def stepLabel = api.currentItem('creationWorkflowStepLabel')
if (quote != null) {
    quoteNumber = quote.uniqueName
    quoteName = quote.label
    quoteTypedId = quote.typedId
}

def isSalesCreationUser = api.isUserInGroup(constants.USER_GROUP_SALES, creationUser)
def isPricingCreationUser = api.isUserInGroup(constants.USER_GROUP_PRICING, creationUser)

def steps = []

def alerts = []

def hasGuardrailError, hasApproverError
lineItems.each {
    hasGuardrailError = calculations.getInputValue(it, lineItemConstants.GUARDRAIL_ERROR_HIDDEN_ID)
    if (hasGuardrailError) alerts.add(hasGuardrailError?.Error)
}

if (alerts) api.throwException(alerts.join(", "))

def quoteInputConfigurator = quote?.inputs?.find { it.name == headerConstants.INPUTS_NAME }?.value
def division = quoteInputConfigurator?.get(headerConstants.DIVISION_ID)
def isSoldToOnly = quoteInputConfigurator?.get(headerConstants.SOLD_TO_ONLY_QUOTE_ID)

def hasFreightValue = lineItems.find { calculations.getInputValue(it, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)?.get(lineItemConstants.FREIGHT_ESTIMATE_ID) }

if (hasFreightValue) {
    def step1 = api.newCreationWorkflowStep()
            .withLabel(constants.USER_GROUP_FREIGHT)
            .withUserGroupAssignee(constants.USER_GROUP_FREIGHT)
            .withShouldSendNotification(false)
    steps.add(step1)

    if (isSalesCreationUser) {
        def step2 = api.newCreationWorkflowStep()
                .withLabel(constants.USER_GROUP_SALES)
                .withUserGroupAssignee(constants.USER_GROUP_SALES)
                .withShouldSendNotification(false)
        steps.add(step2)
    } else if (isPricingCreationUser) {
        def step2 = api.newCreationWorkflowStep()
                .withLabel(constants.USER_GROUP_PRICING)
                .withUserGroupAssignee(constants.USER_GROUP_PRICING)
                .withShouldSendNotification(false)
        steps.add(step2)
    }
} else {

    def step1 = api.newCreationWorkflowStep()
            .withLabel(constants.USER_GROUP_FREIGHT)
            .withUserGroupAssignee(constants.USER_GROUP_FREIGHT)
            .withShouldSendNotification(false)
    steps.add(step1)

    def userGroup = isSalesCreationUser ? constants.USER_GROUP_SALES : constants.USER_GROUP_PRICING
    def step2 = api.newCreationWorkflowStep()
            .withLabel(userGroup)
            .withUserGroupAssignee(userGroup)
            .withShouldSendNotification(false)
    steps.add(step2)
}

def creationWF = api.newCreationWorkflow().withSteps(*steps)

if (!hasFreightValue) creationWF.resetCurrentStepTo(1)

if ((stepNumber == 0 && stepLabel == null) || (stepNumber == 1 && (stepLabel == constants.USER_GROUP_SALES || stepLabel == constants.USER_GROUP_PRICING))) {
    def isStepGroup = stepLabel != null
            ? api.isUserInGroup(stepLabel, user)
            : (api.isUserInGroup(constants.USER_GROUP_SALES, user) || api.isUserInGroup(constants.USER_GROUP_PRICING, user))

    def userGroup = stepLabel ?: "Sales or Pricing"
    def step = stepLabel ? "Finish CW" : "Start CW"

    if (!isStepGroup) api.throwException("Only users with '${userGroup}' user group can ${step}")

    if (hasFreightValue && stepLabel == null) sendEmail(getEmail(constants.CREATION_WORKFLOW_STEP_2_GROUP, creationUser), quoteNumber, quoteName, quoteTypedId, user)

    def requiredInputs = []
    if (api.isUserInGroup(constants.USER_GROUP_SALES, user as String)) requiredInputs.addAll(lineItemConstants.REQUIRED_SALES_INPUTS)
    if (api.isUserInGroup(constants.USER_GROUP_PRICING, user as String)) requiredInputs.addAll(lineItemConstants.REQUIRED_PRICING_INPUTS)
    if (isSoldToOnly) requiredInputs.remove(lineItemConstants.SHIP_TO_ID)
    requiredInputs.unique()

    for (lineItem in lineItems) {
        if (lineItem.folder) continue

        def lineRequiredInputs = []
        lineRequiredInputs.addAll(requiredInputs)

        def configurator = calculations.getInputValue(lineItem, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)
        def priceType = configurator?.get(lineItemConstants.PRICE_TYPE_ID)

        if (priceType == "1") lineRequiredInputs.addAll(lineItemConstants.REQUIRED_PRICE_TYPE_INPUTS_1)
        if (priceType == "2") {
            lineRequiredInputs.addAll(lineItemConstants.REQUIRED_PRICE_TYPE_INPUTS_2)
            lineRequiredInputs.remove(lineItemConstants.PRICE_LIST_ID)
            lineRequiredInputs.add(lineItemConstants.RECOMMENDED_PRICE_ID)
        }
        if (priceType == "3") lineRequiredInputs.addAll(lineItemConstants.REQUIRED_PRICE_TYPE_INPUTS_3)
        if (priceType == "4") lineRequiredInputs.removeAll(lineItemConstants.NOT_REQUIRED_PRICE_TYPE_INPUTS_4)

        if (configurator?.get(lineItemConstants.INCO_TERM_ID) == "FCA") lineRequiredInputs.removeAll(lineItemConstants.REQUIRED_DISTINCT_FCA_INPUTS + lineItemConstants.REQUIRED_FREIGHT_ESTIMATE_INPUTS)
        if (!configurator?.get(lineItemConstants.FREIGHT_ESTIMATE_ID)) lineRequiredInputs.removeAll(lineItemConstants.REQUIRED_FREIGHT_ESTIMATE_INPUTS)

        def found = lineRequiredInputs.findAll {
            if (it == lineItemConstants.PF_CONFIGURATOR_ADDER_ID) {
                return configurator?.get(it) == null
            } else {
                return !configurator?.get(it)
            }
        }
        if (found) {
            def labels = found.join(" ")
            api.throwException("${lineItem?.sku} - Required fields are missing " + labels)
        }
    }
}

if (stepNumber == 1 && stepLabel == constants.USER_GROUP_FREIGHT) {
    def isSecondStepGroup = api.isUserInGroup(constants.CREATION_WORKFLOW_STEP_2_GROUP, user)

    if (!isSecondStepGroup) api.throwException("Only users with 'Freight' user group can modify the quote")

    def userGroup = isSalesCreationUser ? constants.USER_GROUP_SALES : constants.USER_GROUP_PRICING
    sendEmail(getEmail(userGroup, creationUser, division), quoteNumber, quoteName, quoteTypedId, user)

    def requiredInputs = lineItemConstants.REQUIRED_FREIGHT_INPUTS

    for (lineItem in lineItems) {
        if (lineItem.folder) continue

        def configurator = calculations.getInputValue(lineItem, lineItemConstants.NEW_QUOTE_CONFIGURATOR_NAME)

        if (!configurator?.get(lineItemConstants.FREIGHT_ESTIMATE_ID)) continue

        def found = requiredInputs.findAll { !configurator?.get(it) }
        if (found) {
            def labels = found.join(" ")
            api.throwException("${lineItem?.sku} - Required fields are missing " + labels)
        }
    }
}

return creationWF

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