import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

def shouldMaintainOldDates = out.ShouldMaintainOldDates
def freightValidTo

if (shouldMaintainOldDates) {
    freightValidTo = out.LoadConditionRecords.validTo ? sdf.parse(out.LoadConditionRecords.validTo.toString()) : out.LoadQuotes.PriceValidTo
} else {
    freightValidTo = out.CalculateValidToForActiveLines?.NewPriceValidTo ?: api.global.expirationDate
}

return api.attributedResult(freightValidTo).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())