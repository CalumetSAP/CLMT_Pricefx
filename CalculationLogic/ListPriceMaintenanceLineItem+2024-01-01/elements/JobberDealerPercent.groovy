BigDecimal jobberPercent
def jobberDealerOverride = api.local.jobberDealerOverride

if (jobberDealerOverride) {
    jobberPercent = jobberDealerOverride.overriddenValue
} else {
    jobberPercent = out.CalculateJobberDealerPercent ?: api.local.currentContext?.get("JobberDealerPercent")
    if (api.local.jobberDealerPercentError) {
        api.criticalAlert(api.local.jobberDealerPercentError as String)
    }
}

return api.attributedResult(jobberPercent).withBackgroundColor(libs.PricelistLib.Colors.getEditableFieldColor())