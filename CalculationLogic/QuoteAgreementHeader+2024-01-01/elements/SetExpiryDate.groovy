import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final headerConfiguratorConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final dateUtils = libs.QuoteLibrary.DateUtils

def effectiveDate = quoteProcessor.getQuoteView().targetDate
SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd")
Date date = inputDateFormat.parse(effectiveDate)

def days = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceValidTo"]?.values()?.find() as Integer : null

def defaultExpiryDate = dateUtils.sumDays(date, days)

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