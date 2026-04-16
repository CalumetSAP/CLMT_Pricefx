def entry = libs.BdpLib.UserInputs.createInputString(
        "ContractPO",
        "Contract PO#",
        false,
        true
)

entry.getFirstInput().setConfigParameter("maxLength", 35)

return entry