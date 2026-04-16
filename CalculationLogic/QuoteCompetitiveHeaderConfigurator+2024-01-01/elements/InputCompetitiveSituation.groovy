final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = api.local.competitiveSituation && !api.isInputGenerationExecution() ? api.local.competitiveSituation as Map : [:]

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.COMPETITIVE_SITUATION_ID,
        headerConstants.COMPETITIVE_SITUATION_LABEL,
        false,
        false,
        null,
        options as Map
)

return entry