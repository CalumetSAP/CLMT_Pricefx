import net.pricefx.common.api.InputType
import net.pricefx.formulaengine.scripting.inputbuilder.AbstractInputBuilder

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

//quoteProcessor.addOrUpdateInput(
//        "ROOT", [
//        "name" : headerConstants.CONTRACT_PO_ID,
//        "label": headerConstants.CONTRACT_PO_LABEL,
//        "type" : InputType.TEXTUSERENTRY,
//        "width": AbstractInputBuilder.InputWidth.MAX
//])

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.EXTERNAL_NOTES_ID,
        "label": headerConstants.EXTERNAL_NOTES_LABEL,
        "type" : InputType.TEXTUSERENTRY,
        "width": AbstractInputBuilder.InputWidth.MAX
])

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.EXPIRY_DATE_DEFAULTED,
        "type" : InputType.HIDDEN,
])