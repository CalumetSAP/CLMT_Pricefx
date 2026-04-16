def moq = api.global.isFirstLoad ? out.LoadBasePricings?.MOQ?.toBigDecimal() : api.currentItem("MOQ")
if (!moq) moq = null

return api.attributedResult(moq)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())