def pricelists = api.isInputGenerationExecution() ? [] : out.FindPricelists?.collect { it.name + " - " + it.attribute1 }
def options = ["Select All", *pricelists]
def defaultValues = []

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "PricelistInput",
        "Pricelist",
        true,
        false,
        defaultValues,
        options
)
def param = entry.getFirstInput()
if(param.getValue()?.any { it  == "Select All" }) {
    param.setValue(pricelists)
}

return entry