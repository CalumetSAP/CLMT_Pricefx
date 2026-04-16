//Row edition
if ((api.getManualOverride("SRPPercent") || api.getManualOverride("NewSRP")) && !api.global.isFullListRecalc) {
    return new Date()
}

return api.local.currentContext?.get("SRPModifiedDatetime")