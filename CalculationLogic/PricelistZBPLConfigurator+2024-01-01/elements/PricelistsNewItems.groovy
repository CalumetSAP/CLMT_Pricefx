def pricelists = api.isInputGenerationExecution() ? [] : out.FindPricelists
def options = ["Select All", *pricelists]
def defaultValues = []

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "PricelistNewItemsInput",
        "Pricelist (new items)",
        false,
        false,
        defaultValues,
        options
)
def param = entry.getFirstInput()
if (param.getValue()?.any { it  == "Select All" }) {
    param.setValue(pricelists)
}

return entry