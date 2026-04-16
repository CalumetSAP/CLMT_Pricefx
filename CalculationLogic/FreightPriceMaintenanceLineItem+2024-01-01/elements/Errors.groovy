List<String> errors = []

if (!api.global.countries.contains(out.DestinationCountry)) {
    errors.add("Please check Ship to Country (it is not matching with FreightBusinessRulesCountries CPT options)")
}

if (!out.AverageLinehaulRate && out.NewFreightAmount == null) {
    errors.add("There are no rates for this route and New Freight Amount is null")
}

if (api.local.overlappingErrors) {
    errors.addAll(api.local.overlappingErrors)
}

if (errors) {
    String errorMsg = errors.join("; ")
    api.criticalAlert(errorMsg)
    return errorMsg
}
