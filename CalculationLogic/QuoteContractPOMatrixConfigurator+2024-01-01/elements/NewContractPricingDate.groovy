def entry = libs.BdpLib.UserInputs.createInputDate(
        "NewContractPricingDate",
        "New Contract Pricing Date",
        false,
        false
)

input = entry.getFirstInput()

api.logWarn("NewContractPricingDate after", entry?.getFirstInput()?.getValue())


return entry