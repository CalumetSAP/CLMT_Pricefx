import net.pricefx.common.api.InputType

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def previousValues = quoteProcessor.getHelper().getRoot().getInputByName(headerConstants.COMPETITIVE_INFO_NAME)?.value ?: [:]

def competitiveSituation = out.FindDropdownOptions && !api.isInputGenerationExecution() ? out.FindDropdownOptions["CompetitiveSituation"] as Map : [:]

// configurator parameters
def params = [
        CompetitiveSituation: competitiveSituation,
]

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name"          : headerConstants.COMPETITIVE_INFO_NAME,
        "label"         : headerConstants.COMPETITIVE_INFO_LABEL,
        "url"           : headerConstants.COMPETITIVE_INFO_URL,
        "type"          : InputType.INLINECONFIGURATOR,
        "value"         : previousValues + params,
        "parameterGroup": "Competitive Info"
])

return previousValues