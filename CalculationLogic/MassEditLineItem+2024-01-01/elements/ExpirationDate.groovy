Date expirationDate = api.global.expirationDate

//If there is "Protection" value add 1 year to calculated "Effective Date" and use it as "Expiration Date"
if (out.LoadExclusions?.attribute2) {
    Calendar calendar = Calendar.getInstance()
    calendar.setTime(out.EffectiveDate)
    calendar.add(Calendar.YEAR, 1)
    expirationDate = calendar.getTime()
}

return api.attributedResult(expirationDate).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())