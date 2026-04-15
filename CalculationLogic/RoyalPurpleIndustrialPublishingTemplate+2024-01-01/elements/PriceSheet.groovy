import java.text.SimpleDateFormat

String inputDate = "2022-07-18"
SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd")
SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy")

Date date = inputFormat.parse(inputDate)
String pricingDate = outputFormat.format(date)

return [
        "ROYAL PURPLE INDUSTRIAL PRICE LIST",
        "Effective Date: "+pricingDate,
        api.local.terms,
]