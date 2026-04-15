import net.pricefx.common.api.InputType
import net.pricefx.formulaengine.scripting.inputbuilder.AbstractInputBuilder

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnly = !api.local.isNotFreightGroup

def entry = libs.BdpLib.UserInputs.createInputString(
        headerConstants.PRIMARY_CONTACT_ID,
        headerConstants.PRIMARY_CONTACT_LABEL,
        false,
        readOnly
)

entry.getFirstInput().setConfigParameter("width", AbstractInputBuilder.InputWidth.MAX)

return entry