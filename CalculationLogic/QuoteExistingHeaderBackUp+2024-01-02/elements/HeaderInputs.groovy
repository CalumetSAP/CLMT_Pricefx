import net.pricefx.common.api.InputType
import net.pricefx.formulaengine.scripting.inputbuilder.AbstractInputBuilder

if (quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.EXTERNAL_NOTES_ID,
        "label": headerConstants.EXTERNAL_NOTES_LABEL,
        "type" : InputType.TEXTUSERENTRY,
        "width": AbstractInputBuilder.InputWidth.MAX
])

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.EXPIRY_DATE_DEFAULTED,
        "type" : InputType.HIDDEN,
])

def headerConfigurator = quoteProcessor.getHelper().getRoot().getInputByName(
        headerConstants.INPUTS_NAME
)?.value ?: [:]

def contracts = headerConfigurator?.get(headerConstants.CONTRACT_NUMBER_ID) ?: []
def previousContracts = quoteProcessor.getHelper().getRoot().getInputByName(headerConstants.SELECTED_CONTRACTS_HIDDEN)?.value ?: []

def soldTos = headerConfigurator?.get(headerConstants.SOLD_TO_ID) ?: []
def previousSoldTos = quoteProcessor.getHelper().getRoot().getInputByName("SelectedSoldTosHiddenInput")?.value ?: []
def shipTos = headerConfigurator?.get(headerConstants.SHIP_TO_ID) ?: []
def previousShipTos = quoteProcessor.getHelper().getRoot().getInputByName("SelectedShipTosHiddenInput")?.value ?: []

if (contracts != previousContracts) {
    def addedContracts = contracts?.clone() as List
    addedContracts?.removeAll(previousContracts)
    api.local.addedContracts = addedContracts

    def removedContracts = previousContracts?.clone() as List
    removedContracts?.removeAll(contracts)
    api.local.removedContracts = removedContracts
}

if (soldTos != previousSoldTos) {
    def addedSoldTos = soldTos?.clone() as List
    addedSoldTos?.removeAll(previousSoldTos)
    api.local.addedSoldTos = addedSoldTos

    def removedSoldTos = previousSoldTos?.clone() as List
    removedSoldTos?.removeAll(soldTos)
    api.local.removedSoldTos = removedSoldTos
}

if (shipTos != previousShipTos) {
    def addedShipTos = shipTos?.clone() as List
    addedShipTos?.removeAll(previousShipTos)
    api.local.addedShipTos = addedShipTos

    def removedShipTos = previousShipTos?.clone() as List
    removedShipTos?.removeAll(shipTos)
    api.local.removedShipTos = removedShipTos
}

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.SELECTED_CONTRACTS_HIDDEN,
        "value": contracts,
        "type" : InputType.HIDDEN,
])

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : "SelectedSoldTosHiddenInput",
        "value": soldTos,
        "type" : InputType.HIDDEN,
])

quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : "SelectedShipTosHiddenInput",
        "value": shipTos,
        "type" : InputType.HIDDEN,
])