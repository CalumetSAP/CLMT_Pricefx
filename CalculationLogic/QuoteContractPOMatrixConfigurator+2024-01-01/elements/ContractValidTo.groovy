def entry = libs.BdpLib.UserInputs.createInputDate(
        "ContractValidTo",
        "Contract Valid To",
        false,
        false
)

input = entry.getFirstInput()

return entry