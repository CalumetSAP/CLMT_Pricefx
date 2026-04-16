import net.pricefx.common.api.InputType

def havePermissions = true

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputString(
            lineItemConstants.SAP_CONTRACT_ID,
            lineItemConstants.SAP_CONTRACT_LABEL,
            false,
            true,
            api.local.dataSourceData?.SAPContractNumber as String
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.SAP_CONTRACT_ID,
            InputType.HIDDEN,
            lineItemConstants.SAP_CONTRACT_LABEL,
            false,
            true,
    )
    input = entry.getFirstInput()
}

return entry