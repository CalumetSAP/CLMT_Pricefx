def color
if (api.local.product?.attribute6 != api.global.selectedDivision) color = "#ff7152"

return api.attributedResult(api.local.product?.sku)
        .withBackgroundColor(color)