//Row edition
if (api.getManualOverride("NewListPrice") && !api.global.isFullListRecalc) {
    return new Date()
}

return api.local.currentContext?.get("NewListPriceModifiedDatetime")