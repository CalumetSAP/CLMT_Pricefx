if (api.isInputGenerationExecution()) return

if (api.local.priceType == "1" || api.local.priceType == "4") {
    api.throwException("Scales are not applicable for this price type")
}