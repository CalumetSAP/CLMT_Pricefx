List<String> errors = []

if (out.HaulCharges == null && out.NewFreightAmount == null) {
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
