if (!out.IsPLSelected) return null

def scaleQty
if (api.local.isNewProduct) {
    scaleQty = api.global.productsAndScalesMap?.get(api.local.material)?.get(api.local.lineNumber)
} else {
    scaleQty = api.local.zbplScalesMerged.ScaleQuantity
}
return api.attributedResult(scaleQty).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())