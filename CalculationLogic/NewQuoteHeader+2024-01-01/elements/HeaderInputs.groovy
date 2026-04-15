import net.pricefx.common.api.InputType
import net.pricefx.formulaengine.scripting.inputbuilder.AbstractInputBuilder

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final general = libs.QuoteConstantsLibrary.General

def user = api.user("loginName") as String
def readOnly = !(api.isUserInGroup(general.USER_GROUP_SALES, user) || api.isUserInGroup(general.USER_GROUP_PRICING, user))

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name"    : headerConstants.EXTERNAL_NOTES_ID,
        "label"   : headerConstants.EXTERNAL_NOTES_LABEL,
        "type"    : InputType.HIDDEN,
])

quoteProcessor.setRenderInfo("externalRef", "disabled", readOnly)

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def divisionMap = out.FindDivision

def division = headerConfigurator?.get(headerConstants.DIVISION_ID)
def completeDivision = division ? divisionMap?.get(division) : null
quoteProcessor.updateField("attributeExtension___Division", completeDivision)