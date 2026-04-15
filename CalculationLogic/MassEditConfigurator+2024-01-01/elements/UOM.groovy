def priceChangePerUOM = out.PriceChangePerUOM?.getFirstInput()?.getValue()

def uomList = ["LB", "KG", "UG6"]
def options = [*uomList]

def entry = libs.BdpLib.UserInputs.createInputOption(
        "UOMInput",
        "UOM",
        false,
        true,
        options
)
def param = entry.getFirstInput()

if(priceChangePerUOM) {
    param.setRequired(true)
    param.setReadOnly(false)
}else{
    param.setRequired(false)
    param.setReadOnly(true)
    param.setValue(null)
}

return entry