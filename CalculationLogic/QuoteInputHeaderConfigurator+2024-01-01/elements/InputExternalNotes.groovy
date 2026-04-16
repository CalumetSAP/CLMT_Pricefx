import net.pricefx.common.api.InputType
import net.pricefx.formulaengine.scripting.inputbuilder.AbstractInputBuilder

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnly = !api.local.isNotFreightGroup

def entry = libs.BdpLib.UserInputs.createInput(
        headerConstants.EXTERNAL_NOTES_ID,
        InputType.TEXTUSERENTRY,
        headerConstants.EXTERNAL_NOTES_LABEL,
        false,
        readOnly
)

entry.getFirstInput().setConfigParameter("width", AbstractInputBuilder.InputWidth.MAX)
entry.getFirstInput().setConfigParameter("noRefresh", true)

return entry
