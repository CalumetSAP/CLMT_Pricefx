def salesOrgs = api.isInputGenerationExecution() ? [] : out.FindSalesOrgs?.collect { it.name + " - " + it.attribute1 }
//def options = ["Select All", *salesOrgs]

def entry = libs.BdpLib.UserInputs.createInputOptions(
        "SalesOrgInput",
        "Sales Org",
        true,
        false,
        [],
        salesOrgs
)
//def param = entry.getFirstInput()
//if(param.getValue()?.any { it  == "Select All" }) {
//    param.setValue(salesOrgs)
//}

return entry