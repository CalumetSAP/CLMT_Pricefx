//Row edition
if ((api.getManualOverride("MAPPercent") || api.getManualOverride("NewMapPrice")) && !api.global.isFullListRecalc) {
    return new Date()
}

return api.local.currentContext?.get("MAPModifiedDatetime")