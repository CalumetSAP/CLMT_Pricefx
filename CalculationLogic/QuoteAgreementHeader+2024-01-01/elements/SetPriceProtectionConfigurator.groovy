//import net.pricefx.common.api.InputType
//
//if (quoteProcessor.isPostPhase()) return
//
//final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
//final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
//final calculations = libs.QuoteLibrary.Calculations
//
//def priceProtectionOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceProtection"] as Map : [:]
//def movementTimingOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["MovementTiming"] as Map : [:]
//def productMasterData = out.FindProductMasterData ?: [:]
//def exclusionsMap = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindExclusions : null
//
//def customerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
//        headerConstants.INPUTS_NAME
//)?.value ?: [:]
//
//def selectedSoldTo = customerConfigurator?.get(headerConstants.SOLD_TO_ID)
//
//def params = [
//        PriceProtection: priceProtectionOptions,
//        MovementTiming : movementTimingOptions,
//]
//
//for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
//    if (lnProduct.folder) continue
//
//    def readOnly = calculations.getInputValue(lnProduct, lineItemConstants.PRICE_TYPE_ID) != "2"
//
//    def productMasterItem = productMasterData?.get(lnProduct.sku)
//
//    def ph1 = productMasterItem?.PH1Code
//    def shipTo = calculations.getInputValue(lnProduct, lineItemConstants.SHIP_TO_ID)
//
//    def exclusionData = calculations.getPriceProtectionDataByExclusion(exclusionsMap, selectedSoldTo, ph1, shipTo, lnProduct.sku)
//
//    def conditionalParams = [
//            ReadOnly     : readOnly,
//            ExclusionData: exclusionData
//    ]
//
//    def previousValues = lnProduct.inputs.find {
//        lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
//    }?.value ?: [:]
//
//    quoteProcessor.addOrUpdateInput(lnProduct.lineId as String, [
//            "name"    : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
//            "label"   : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
//            "type"    : InputType.CONFIGURATOR,
//            "url"     : lineItemConstants.PRICE_PROTECTION_CONFIGURATOR_URL,
//            "value"   : previousValues + params + conditionalParams,
//    ])
//}
//
//return null
