import net.pricefx.common.api.InputButtonAction
import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution()) return

final userInputs = libs.BdpLib.UserInputs

def layout = api.createConfiguratorEntry()
def inputList = []
userInputs.with {

    inputList = []
    if (isNotHidden(out.InputShipToMatrix)) inputList.add(InputShipToMatrix.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.InputMaterial)) inputList.add(InputMaterial.input)
    if (isNotHidden(out.InputDescription)) inputList.add(InputDescription.input)
    if (isNotHidden(out.InputPH1)) inputList.add(InputPH1.input)
    if (isNotHidden(out.InputPH2)) inputList.add(InputPH2.input)
    if (isNotHidden(out.InputPH3)) inputList.add(InputPH3.input)
    if (isNotHidden(out.InputPH4)) inputList.add(InputPH4.input)
    if (isNotHidden(out.InputMaterialPackageStyle)) inputList.add(InputMaterialPackageStyle.input)
    if (isNotHidden(out.InputLegacyMaterialNumber)) inputList.add(InputLegacyMaterialNumber.input)
    if (isNotHidden(out.InputPlant)) inputList.add(InputPlant.input)
    if (isNotHidden(out.InputShippingPoint)) inputList.add(InputShippingPoint.input)
    if (isNotHidden(out.InputMOQ)) inputList.add(InputMOQ.input)
    if (isNotHidden(out.InputMOQUOM)) inputList.add(InputMOQUOM.input)
    if (isNotHidden(out.InputMeansOfTransportation)) inputList.add(InputMeansOfTransportation.input)
    if (isNotHidden(out.InputModeOfTransportation)) inputList.add(InputModeOfTransportation.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputPriceType)) inputList.add(InputPriceType.input)
    if (isNotHidden(out.InputPricelist)) inputList.add(InputPricelist.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.InputRecommendedPrice)) inputList.add(InputRecommendedPrice.input)
    if (isNotHidden(out.InputCompetitorPrice)) inputList.add(InputCompetitorPrice.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputPrice)) inputList.add(InputPrice.entry.getFirstInput())
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.InputPricingUOM)) inputList.add(InputPricingUOM.input)
    if (isNotHidden(out.InputNumberOfDecimals)) inputList.add(InputNumberOfDecimals.input)
    if (isNotHidden(out.InputPer)) inputList.add(InputPer.input)
    if (isNotHidden(out.InputCurrency)) inputList.add(InputCurrency.input)
    if (isNotHidden(out.InputPriceValidFrom)) inputList.add(InputPriceValidFrom.input)
    if (isNotHidden(out.InputPriceValidTo)) inputList.add(InputPriceValidTo.input)
    if (isNotHidden(out.InputCost)) inputList.add(InputCost.input)
    if (isNotHidden(out.InputMaterialMargin)) inputList.add(InputMaterialMargin.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputSalesPerson)) inputList.add(InputSalesPerson.input)
    if (isNotHidden(out.InputRejectionReason)) inputList.add(InputRejectionReason.input)
    setEntryInputsInColumns(layout, 1, inputList)
}

// Hide Clear button (Breaks dropdowns)
layout?.setHiddenActions(InputButtonAction.CLEAR)

return layout

def isNotHidden(entry) {
    if (!entry) return true
    return !(entry?.getFirstInput()?.getInputType() == InputType.HIDDEN)
}