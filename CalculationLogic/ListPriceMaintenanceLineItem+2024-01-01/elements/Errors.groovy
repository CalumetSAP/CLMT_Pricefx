List<String> errors = []

if (out.Cost == null) {
    errors.add("No cost exists for this material")
}

if (errors) {
    String errorMsg = errors.join("; ")
    api.criticalAlert(errorMsg)
    return errorMsg
}

return null