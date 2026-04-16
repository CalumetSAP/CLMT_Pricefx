final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def allSalesOrg = api.local.salesOrg ? api.local.salesOrg as Map : [:]
def selectedSalesOrg = out.FindSalesOrg ? out.FindSalesOrg as List : []
def options = allSalesOrg?.findAll { key, value -> selectedSalesOrg.contains(key) }
def readOnly = !(out.InputDivision?.getFirstInput()?.getValue() && api.local.isNotFreightGroup)

api.local.inputSalesOrgHasChange = false
def callback = { value -> api.local.inputSalesOrgHasChange = true }

def entry = libs.BdpLib.UserInputs.createInputOption(
        headerConstants.SALES_ORG_ID,
        headerConstants.SALES_ORG_LABEL,
        true,
        readOnly,
        null,
        options
)

libs.BdpLib.UserInputs.addCallbackConfigurationOnChange(
        entry,
        callback
)

if (api.local.inputSoldToHasChange || api.local.inputDivisionHasChange) {
    entry.getFirstInput().setValue(null)
}

if (options.size() == 1) {
    entry.getFirstInput().setValue(options?.keySet()?.find())
}

return entry