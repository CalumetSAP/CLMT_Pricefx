import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

return out.LoadConditionRecords.validTo ? sdf.parse(out.LoadConditionRecords.validTo.toString()) : out.LoadQuotes.PriceValidTo