def meansOfTransportation = api.isInputGenerationExecution() ? [:] : out.FindMeansOfTransportation

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "MeansOfTransportationInput",
        "Means of Transportation",
        false,
        false,
        null,
        meansOfTransportation
)

return entry