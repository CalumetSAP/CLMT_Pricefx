//if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase() ) return
//
//final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
//final calculations = libs.QuoteLibrary.Calculations
//
//def dropdownOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions as Map : [:]
//
//for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
//    if (lnProduct.folder) continue
//    def useConfiguratorValue = api.local.lineItemChanged == lnProduct.lineId
//
//    def configurator = calculations.getInputValue(lnProduct, lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME)
//    def dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
//
//    def priceTypeAux = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID)
//    def priceType = useConfiguratorValue
//            ? configurator?.get(lineItemConstants.PRICE_TYPE_ID)
//            : priceTypeAux ? dropdownOptions["PriceType"]?.find { k, v -> v.toString().startsWith(priceTypeAux) }?.key : priceTypeAux
//    if (priceType != "2") continue
//
//    def configuratorValues = lnProduct.inputs.find {
//        lineItemConstants.SCALES_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
//    }?.value ?: [:]
//
//    def scalesValues = configuratorValues?.get(lineItemConstants.SCALES_ID) ?: []
//
//    def key = dsData?.QuoteID + "|" + dsData?.LineID
//    def pricingData = out.FindQuotesDS?.get(key)
//    def scalesData = calculations.getScalesData(pricingData, out.FindQuoteScales)
//    def pricingScales = []
//    if (scalesData) {
//        scalesData?.each { data ->
//            def scale = [
//                    ScaleQty: data.ScaleQuantity,
//                    ScaleUOM: pricingData?.UOM,
//                    Price   : data.ConditionRate,
//                    PriceUOM: pricingData?.UOM,
//            ]
//            pricingScales.add(scale)
//        }
//    }
//
//    pricingScales = sortByScaleQty(pricingScales)
//    scalesValues = sortByScaleQty(scalesValues)
//    def hasChanges = hasChanges(pricingScales, scalesValues)
//    updateInputValue(lnProduct.lineId, lineItemConstants.SCALES_HAS_CHANGED_ID, hasChanges)
//}
//
//return
//
//def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
//    final calculations = libs.QuoteLibrary.Calculations
//
//    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
//}
//
//def hasChanges(originalList, modifiedList) {
//    if (originalList.size() != modifiedList.size()) return true
//
//    def changesFound = false
//    for (int i = 0; i < originalList.size(); i++) {
//        def originalMap = originalList[i]
//        def modifiedMap = modifiedList[i]
//
//        originalMap.each { key, originalValue ->
//            if (originalValue != modifiedMap[key]) changesFound = true
//        }
//    }
//
//    return changesFound
//}
//
//def sortByScaleQty(list) {
//    list.sort { it.ScaleQty }
//}
//
//def updateInputValue(String lineId, name, defaultValue) {
//    quoteProcessor.addOrUpdateInput(
//            lineId, [
//            "name"        : name,
//            "value"       : defaultValue,
//    ])
//}