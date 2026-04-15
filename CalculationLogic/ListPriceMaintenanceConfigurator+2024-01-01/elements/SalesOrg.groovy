def salesOrgs = api.isInputGenerationExecution() ? [] : out.FindSalesOrgs?.collect { it.name + " - " + it.attribute1 }

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "SalesOrgInput",
        "Sales Org",
        true,
        false,
        [],
        salesOrgs
)

return entry