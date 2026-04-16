import net.pricefx.common.api.FieldFormatType

def rateRows = out.LoadTruckRateUploadPX.RateRows

if (!rateRows) return null

List<Map<String, Object>> result = []

rateRows.each {
    result.add(buildRow(it.Carrier, it.CarrierSCAC, it.Match, it.CarrierDescription, it."Valid From", it.ValidTo, it.Rate as BigDecimal, it.RateCurrency, it.Consignee))
}

return api.newMatrix()
        .withColumnFormats([
                "Carrier"            : FieldFormatType.TEXT,
                "Carrier SCAC"       : FieldFormatType.TEXT,
                "Match"              : FieldFormatType.TEXT,
                "Carrier Description": FieldFormatType.TEXT,
                "Valid From"         : FieldFormatType.DATE,
                "Valid To"           : FieldFormatType.DATE,
                "Rate"               : FieldFormatType.MONEY,
                "Rate Currency"      : FieldFormatType.TEXT,
                "Consignee"          : FieldFormatType.TEXT,
        ])
        .withEnableClientFilter(true)
        .withRows(result)

Map<String, Object> buildRow(String carrier, String carrierSCAC, String match, String carrierDesc, validFrom, validTo, BigDecimal rate, String rateCurrency, String consignee) {
    return [
            "Carrier"            : carrier,
            "Carrier SCAC"       : carrierSCAC,
            "Match"              : match,
            "Carrier Description": carrierDesc,
            "Valid From"         : validFrom,
            "Valid To"           : validTo,
            "Rate"               : rate,
            "Rate Currency"      : rateCurrency,
            "Consignee"          : consignee
    ]
}