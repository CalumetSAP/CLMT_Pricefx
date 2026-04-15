import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

// hide default Customer input
quoteProcessor.addOrUpdateInput("ROOT", [
        "name"    : "Customer",
        "label"   : "Customer",
        "readOnly": true,
        "type"    : InputType.HIDDEN,
])

def previousValues = quoteProcessor.getHelper().getRoot().getInputByName(headerConstants.INPUTS_NAME)?.value ?: [:]

// add custom input
quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.INPUTS_NAME,
        "label": headerConstants.INPUTS_LABEL,
        "url"  : headerConstants.EXISTING_INPUTS_URL,
        "type" : InputType.INLINECONFIGURATOR,
        "value": previousValues,
])

return previousValues