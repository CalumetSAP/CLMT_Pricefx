if (!out.IsPLSelected) return null

def scaleQtyUOM
if (api.local.isNewProduct) {
    scaleQtyUOM = out.ZLISMerged.UOM
} else {
    scaleQtyUOM = out.ZBPLMerged.UOM
}
return api.attributedResult(scaleQtyUOM)
        .withManualOverrideValueOptions(out.LoadUOMOptions as List)
        .withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())