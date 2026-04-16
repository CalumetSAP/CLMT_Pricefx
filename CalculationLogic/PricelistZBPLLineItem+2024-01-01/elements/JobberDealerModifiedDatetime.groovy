//Row edition
if ((api.getManualOverride("JobberDealerPercent") || api.getManualOverride("NewJobberDealerPrice")) && !api.global.isFullListRecalc) {
    return new Date()
}

return api.local.currentContext?.get("JobberDealerModifiedDatetime")