def moqUOM = api.global.isFirstLoad ? out.LoadBasePricings?.MOQUOM : api.currentItem("MOQ UOM")

def overridedMOQ = api.getManualOverride("MOQ")?.toBigDecimal()
if (overridedMOQ == BigDecimal.ZERO) {
    api.removeManualOverride("MOQUOM")
    moqUOM = null
}

return api.attributedResult(moqUOM)
        .withManualOverrideValueOptions(out.LoadUOMOptions as List)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())