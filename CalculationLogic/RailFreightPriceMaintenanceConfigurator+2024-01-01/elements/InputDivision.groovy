def divisions = api.isInputGenerationExecution() ? [:] : out.FindDivisions

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "DivisionInput",
        "Division",
        false,
        false,
        null,
        divisions
)

return entry