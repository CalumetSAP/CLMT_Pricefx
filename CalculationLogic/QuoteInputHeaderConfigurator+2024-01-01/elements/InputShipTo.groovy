if (out.InputSoldToOnlyQuote?.getFirstInput()?.getValue()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def options = out.FindShipToOptions ? ["Select All", *(out.FindShipToOptions as List)] : []
def readOnly = !(out.InputSalesOrg?.getFirstInput()?.getValue() && api.local.isNotFreightGroup)

def entry = libs.BdpLib.UserInputs.createInputOptions(
        headerConstants.SHIP_TO_ID,
        headerConstants.SHIP_TO_LABEL,
        false,
        readOnly,
        null,
        options
)

if(entry.getFirstInput().getValue()?.any{ it  == "Select All" }) {
    entry.getFirstInput().setValue(out.FindShipToOptions as List)
}

if (api.local.inputSoldToHasChange || api.local.inputDivisionHasChange || api.local.inputSalesOrgHasChange) {
    entry.getFirstInput().setValue(null)
}

if (options.size() == 2) {
    entry.getFirstInput().setValue(out.FindShipToOptions as List)
}

return entry