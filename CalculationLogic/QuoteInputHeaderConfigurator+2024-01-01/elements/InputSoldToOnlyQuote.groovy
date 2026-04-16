import net.pricefx.common.api.InputType

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnly = !api.local.isNotFreightGroup

//def entry = libs.BdpLib.UserInputs.createInputCheckbox(
//        headerConstants.SOLD_TO_ONLY_QUOTE_ID,
//        headerConstants.SOLD_TO_ONLY_QUOTE_LABEL,
//        false,
//        readOnly,
//        false
//)

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, headerConstants.SOLD_TO_ONLY_QUOTE_ID)
entry.getFirstInput().setValue(false)

return entry
