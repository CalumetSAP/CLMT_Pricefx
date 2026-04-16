import net.pricefx.common.api.InputButtonAction
import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution()) return

final userInputs = libs.BdpLib.UserInputs

def priceType = InputPriceType?.input?.getValue()

def layout = api.createConfiguratorEntry()
def inputList = []
userInputs.with {

    def recommendedList = []
    if (isNotHidden(out.InputRecommendedPrice)) recommendedList.add(InputRecommendedPrice.input)
    if (isNotHidden(out.InputCompetitorPrice)) recommendedList.add(InputCompetitorPrice.input)

    inputList = []
    if (isNotHidden(out.InputMaterial)) inputList.add(InputMaterial.input)
    if (isNotHidden(out.InputDescription)) inputList.add(InputDescription.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputPH1)) inputList.add(InputPH1.input)
    if (isNotHidden(out.InputPH2)) inputList.add(InputPH2.input)
    if (isNotHidden(out.InputPH3)) inputList.add(InputPH3.input)
    if (isNotHidden(out.InputPH4)) inputList.add(InputPH4.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputMaterialPackageStyle)) inputList.add(InputMaterialPackageStyle.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.InputCustomerMaterialNumber)) inputList.add(InputCustomerMaterialNumber.input)
    if (isNotHidden(out.InputLegacyMaterialNumber)) inputList.add(InputLegacyMaterialNumber.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputShipTo)) inputList.add(InputShipTo.input)
    if (api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup) inputList.add(InputShipToAddress.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (api.local.isSalesGroup || api.local.isPricingGroup) {
        if (isNotHidden(out.InputShipToIndustry)) inputList.add(InputShipToIndustry.input)
        if (isNotHidden(out.InputShipToAddress)) inputList.add(InputShipToAddress.input)
    }
    if (isNotHidden(out.InputShipToCity)) inputList.add(InputShipToCity.input)
    if (isNotHidden(out.InputShipToState)) inputList.add(InputShipToState.input)
    if (isNotHidden(out.InputShipToZip)) inputList.add(InputShipToZip.input)
    if (isNotHidden(out.InputShipToCountry)) inputList.add(InputShipToCountry.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup) {
        if (isNotHidden(out.InputNamedPlace)) inputList.add(InputNamedPlace.input)
    }
    if (isNotHidden(out.InputThirdPartyCustomer)) inputList.add(InputThirdPartyCustomer.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup) {
        if (isNotHidden(out.InputFreightTerm)) inputList.add(InputFreightTerm.input)
        if (isNotHidden(out.InputIncoterm)) inputList.add(InputIncoterm.input)
    }
    if (isNotHidden(out.InputPlant)) inputList.add(InputPlant.input)
    if (isNotHidden(out.InputShippingPoint)) inputList.add(InputShippingPoint.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputMOQ)) inputList.add(InputMOQ.input)
    if (isNotHidden(out.InputMOQUOM)) inputList.add(InputMOQUOM.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputSalesShippingMethod)) inputList.add(InputSalesShippingMethod.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.InputMeansOfTransportation)) inputList.add(InputMeansOfTransportation.input)
    if (isNotHidden(out.InputModeOfTransportation)) inputList.add(InputModeOfTransportation.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputPriceType)) inputList.add(InputPriceType.input)
    if (isNotHidden(out.InputIndexIndicator)) inputList.add(InputIndexIndicator.input)
    if (isNotHidden(out.InputPricelist)) inputList.add(InputPricelist.input)
    setEntryInputsInColumns(layout, 1, inputList)

    if (api.local.isFreightGroup && !api.local.isSalesGroup && !api.local.isPricingGroup) {
        inputList = []
        if (isNotHidden(out.InputNumberOfDecimals)) inputList.add(InputNumberOfDecimals.input)
        setEntryInputsInColumns(layout, 1, inputList)
    }

    inputList = []
    if (priceType != "1") inputList.addAll(recommendedList)
    if (isNotHidden(out.InputPrice)) inputList.add(InputPrice.entry.getFirstInput())
    if (isNotHidden(out.InputDeliveredPrice)) inputList.add(InputDeliveredPrice.entry.getFirstInput())
    if (isNotHidden(out.InputPricingUOM)) inputList.add(InputPricingUOM.input)
    if (api.local.isSalesGroup || api.local.isPricingGroup) {
        if (isNotHidden(out.InputNumberOfDecimals)) inputList.add(InputNumberOfDecimals.input)
    }
    if (isNotHidden(out.InputPer)) inputList.add(InputPer.input)
    if (isNotHidden(out.InputCurrency)) inputList.add(InputCurrency.input)
    if (isNotHidden(out.InputPriceValidFrom)) inputList.add(InputPriceValidFrom.input)
    if (isNotHidden(out.InputPriceValidTo)) inputList.add(InputPriceValidTo.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.PFIndexNumber)) inputList.add(PFIndexNumber.entry.getFirstInput())
    if (isNotHidden(out.PFReferencePeriod)) inputList.add(PFReferencePeriod.entry.getFirstInput())
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.PFAdder)) inputList.add(PFAdder.entry.getFirstInput())
    if (isNotHidden(out.PFAdderUOM)) inputList.add(PFAdderUOM.entry.getFirstInput())
    if (isNotHidden(out.PFRecalculationDate)) inputList.add(PFRecalculationDate.input)
    if (isNotHidden(out.PFRecalculationPeriod)) inputList.add(PFRecalculationPeriod.input)
    if (priceType == "1") inputList.addAll(recommendedList)
    if (isNotHidden(out.InputCost)) inputList.add(InputCost.input)
    if (isNotHidden(out.InputMaterialMargin)) inputList.add(InputMaterialMargin.input)
    if (api.local.isSalesGroup || api.local.isPricingGroup) {
        if (isNotHidden(out.InputFreightTerm)) inputList.add(InputFreightTerm.input)
        if (isNotHidden(out.InputIncoterm)) inputList.add(InputIncoterm.input)
    }
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (api.local.isSalesGroup || api.local.isPricingGroup) {
        if (isNotHidden(out.InputNamedPlace)) inputList.add(InputNamedPlace.input)
    }
    if (isNotHidden(out.InputFreightEstimate)) inputList.add(InputFreightEstimate.input)
    setEntryInputsInColumns(layout, 1, inputList)

    inputList = []
    if (isNotHidden(out.InputFreightUOM)) inputList.add(InputFreightUOM.input)
    if (isNotHidden(out.InputFreightAmount)) inputList.add(InputFreightAmount.entry.getFirstInput())
    if (isNotHidden(out.InputFreightValidFrom)) inputList.add(InputFreightValidFrom.input)
    if (isNotHidden(out.InputFreightValidTo)) inputList.add(InputFreightValidTo.input)
    setEntryInputsInColumns(layout, 2, inputList)

    inputList = []
    if (isNotHidden(out.InputSalesPerson)) inputList.add(InputSalesPerson.input)
    setEntryInputsInColumns(layout, 1, inputList)
}

// Hide Clear button (Breaks dropdowns)
layout?.setHiddenActions(InputButtonAction.CLEAR)

return layout

def isNotHidden(entry) {
    if (!entry) return true
    return !(entry?.getFirstInput()?.getInputType() == InputType.HIDDEN)
}