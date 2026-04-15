if (!out.IsPLSelected) return null

if (api.local.isNewProduct) {
    return api.attributedResult(out.ZLISMerged.UOM)
            .withManualOverrideValueOptions(out.LoadUOMOptions as List)
            .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())
}

return out.ZBPLMerged.UOM