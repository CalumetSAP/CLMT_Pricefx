import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final headerConfiguratorConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final dateUtils = libs.QuoteLibrary.DateUtils

SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd")

def creationDateString = quoteProcessor.getQuoteView().createDate
Date creationDate = inputDateFormat.parse(creationDateString)
quoteProcessor.updateField("targetDate", creationDate)

def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["ExpiryDate"]?.values()?.find() as Integer : null
def defaultExpiryDate = dateUtils.sumDays(creationDate, days)

def hasBeenDefaulted = quoteProcessor.getHelper().getRoot().getInputByName(headerConfiguratorConstants.EXPIRY_DATE_DEFAULTED)?.value
if (!hasBeenDefaulted) {
    quoteProcessor.updateField("expiryDate", defaultExpiryDate)
    quoteProcessor.addOrUpdateInput(
            "ROOT", [
            "name" : headerConfiguratorConstants.EXPIRY_DATE_DEFAULTED,
            "type" : InputType.HIDDEN,
            "value": true
    ])
}

return null