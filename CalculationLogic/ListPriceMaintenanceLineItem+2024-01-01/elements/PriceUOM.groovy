if (api.local.isNewProduct) {
    return api.attributedResult(null)
            .withManualOverrideValueOptions(out.LoadUOMOptions as List)
            .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())
}

return out.ZLISMerged.UOM