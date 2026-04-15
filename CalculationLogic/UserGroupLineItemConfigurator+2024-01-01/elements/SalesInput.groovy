if (!(api.local.isSalesGroup || api.local.isPricingGroup)) return

def readOnly = !api.local.isSalesGroup

def entry = libs.BdpLib.UserInputs.createInputString(
        "SalesInput",
        "Sales Input",
        false,
        readOnly
)

return entry