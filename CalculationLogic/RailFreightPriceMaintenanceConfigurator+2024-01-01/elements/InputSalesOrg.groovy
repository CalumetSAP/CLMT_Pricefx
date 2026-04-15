def salesOrgs = api.isInputGenerationExecution() ? [:] : out.FindSalesOrgs

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "SalesOrgInput",
        "Sales Org",
        false,
        false,
        null,
        salesOrgs
)

return entry