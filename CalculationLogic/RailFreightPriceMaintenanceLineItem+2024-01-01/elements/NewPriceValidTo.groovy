import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def shouldMaintainOldDates = out.ShouldMaintainOldDates
def priceValidTo

if (shouldMaintainOldDates) {
    priceValidTo = out.LoadConditionRecords.validTo ? sdf.parse(out.LoadConditionRecords.validTo.toString()) : out.LoadQuotes.PriceValidTo
} else {
    priceValidTo = out.CalculateValidToForActiveLines?.NewPriceValidTo ?: api.global.expirationDate
}

return api.attributedResult(priceValidTo).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())