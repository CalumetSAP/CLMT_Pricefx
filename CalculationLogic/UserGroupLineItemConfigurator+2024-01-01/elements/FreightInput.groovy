if (!(api.local.isFreightGroup || api.local.isSalesGroup)) return

def readOnly = !api.local.isFreightGroup

def entry = libs.BdpLib.UserInputs.createInputString(
        "FreightInput",
        "Freight Input",
        false,
        readOnly
)

return entry