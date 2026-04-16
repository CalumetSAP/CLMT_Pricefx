import net.pricefx.common.api.InputButtonAction
import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution()) return

final userInputs = libs.BdpLib.UserInputs

def layout = api.createConfiguratorEntry()
def inputList = []
userInputs.with {

    inputList = []
    if (isNotHidden(out.InputSAPContractNumber)) inputList.add(InputSAPContractNumber.input)
    if (isNotHidden(out.InputSAPLineId)) inputList.add(InputSAPLineId.input)
    if (isNotHidden(out.InputMaterialNumber)) inputList.add(InputMaterialNumber.input)
    if (isNotHidden(out.InputMaterialDescription)) inputList.add(InputMaterialDescription.input)
    if (isNotHidden(out.InputShipTo)) inputList.add(InputShipTo.input)
    if (isNotHidden(out.InputPlant)) inputList.add(InputPlant.input)
    if (isNotHidden(out.InputIncoterm)) inputList.add(InputIncoterm.input)
    if (isNotHidden(out.InputFreightTerm)) inputList.add(InputFreightTerm.input)
    setEntryInputsInColumns(layout, 1, inputList)
}

// Hide Clear button (Breaks dropdowns)
layout?.setHiddenActions(InputButtonAction.CLEAR)

return layout

def isNotHidden(entry) {
    if (!entry) return true
    return !(entry?.getFirstInput()?.getInputType() == InputType.HIDDEN)
}