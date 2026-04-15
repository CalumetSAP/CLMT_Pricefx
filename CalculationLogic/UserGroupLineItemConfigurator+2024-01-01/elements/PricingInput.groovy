if (!(api.local.isPricingGroup || api.local.isFreightGroup)) return

def readOnly = !api.local.isPricingGroup

def entry = libs.BdpLib.UserInputs.createInputString(
        "PricingInput",
        "Pricing Input",
        false,
        readOnly
)

return entry