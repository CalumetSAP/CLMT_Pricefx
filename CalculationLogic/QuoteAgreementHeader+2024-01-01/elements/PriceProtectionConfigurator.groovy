//import net.pricefx.common.api.InputType
//
//if (quoteProcessor.isPostPhase()) return
//
//final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
//
//def priceProtectionOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["PriceProtection"] as Map : [:]
//def movementTimingOptions = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["MovementTiming"] as Map : [:]
//
//def previousValues = quoteProcessor.getHelper().getRoot().getInputByName(headerConstants.PRICE_PROTECTION_CONFIGURATOR_NAME)?.value ?: [:]
//
//// configurator parameters
//def params = [
//        PriceProtection: priceProtectionOptions,
//        MovementTiming : movementTimingOptions,
//]
//
////quoteProcessor.addOrUpdateInput(
////        "ROOT", [
////        "name"          : headerConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
////        "label"         : headerConstants.PRICE_PROTECTION_CONFIGURATOR_LABEL,
////        "url"           : headerConstants.PRICE_PROTECTION_CONFIGURATOR_URL,
////        "type"          : InputType.INLINECONFIGURATOR,
////        "value"         : previousValues,
////        "parameterGroup": "Price Protection",
////])
//
//def inputFieldSection = api.inputBuilderFactory()
//        .createCollapseLayout("PriceProtection")
//        .setLabel("Price Protection")
//
//        .addInput(
//                api.inputBuilderFactory()
//                        .createConfiguratorInputBuilder(
//                                headerConstants.PRICE_PROTECTION_CONFIGURATOR_NAME,
//                                headerConstants.PRICE_PROTECTION_CONFIGURATOR_URL,
//                                true
//                        )
//                        .setValue(previousValues + params)
//                        .buildContextParameter()
//        )
//
//        .buildMap()
//
//quoteProcessor.addOrUpdateInput("ROOT", inputFieldSection)
//
//return previousValues