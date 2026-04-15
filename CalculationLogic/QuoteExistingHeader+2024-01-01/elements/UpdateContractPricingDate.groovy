import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

if (api.isInputGenerationExecution() || quoteProcessor.isPostPhase()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final calculations = libs.QuoteLibrary.Calculations
def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

def useConfiguratorValue, configuratorValues, dsData, sapContract, validFrom
def contractPricingDateMap = [:]
for (lnProduct in quoteProcessor.getQuoteView().lineItems) {
    if (lnProduct.folder) continue
    String lineId = lnProduct.lineId as String
    useConfiguratorValue = api.local.lineItemChanged == lineId

//    if (calculations.getInputValue(lnProduct, lineItemConstants.REJECTION_REASON_ID)) continue

    configuratorValues = lnProduct.inputs.find {
        lineItemConstants.EXISTING_QUOTE_CONFIGURATOR_NAME.equalsIgnoreCase(it.name)
    }?.value ?: [:]

    dsData = calculations.getInputValue(lnProduct, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)

    sapContract = dsData?.get("SAPContractNumber")
    sapLineId = dsData?.get("LineId")
    validFrom = findValue(useConfiguratorValue, lineItemConstants.PRICE_VALID_FROM_ID, lnProduct, configuratorValues)

    if (!contractPricingDateMap[sapContract] || contractPricingDateMap[sapContract] < validFrom) {
        contractPricingDateMap.put(sapContract, validFrom)
    }
}

def headerInputConfiguratorValues = quoteProcessor.getHelper().getRoot().getInputByName(headerConstants.INPUTS_NAME)?.value ?: [:]

// TODO: This could be deleted because it's not in use from configurator
headerInputConfiguratorValues?.put("ContractPricingDate", contractPricingDateMap)

def contracts = out.FindContractsForConfigurator as Map
def contractsDSData = api.local.contractsDSData

def contractPOMatrixValues = headerInputConfiguratorValues?.get(headerConstants.CONTRACT_PO_ID) // input.getValue() => remaped from InputContractPO Element
def contractHiddenInput = headerInputConfiguratorValues?.get(headerConstants.CONTRACT_NUMBER_ID) // Contract Number + Sold To => configurator hidden input

def value, data, maxLinePricingValidFrom
def values = []
def headerHasChangedMap = [:]

contractHiddenInput?.each { key ->
    // Get Contract from DS
    value = contracts?.get(key)
    if (!value) return

    // Find actual matrix values
    def prevContractPO = contractPOMatrixValues?.data?.find {
        it.SAPContractNumber == value.SAPContractNumber &&
                it.SoldTo == value.SoldTo &&
                it.ShipTo == value.ShipTo
    }

    // Find in Quote DS the latest price valid from, it couldn't be in the quote because of a line deletion.
    def lastValidFromNotOnQuote = contractsDSData?.findAll{
        it.SAPContractNumber == value.SAPContractNumber &&
                it.SoldTo == value.SoldTo &&
                it.ShipTo == value.ShipTo &&
                it.PriceValidFrom

    }?.collect{it.PriceValidFrom}?.sort{a, b -> dateFormat.parse(b?.toString()) <=> dateFormat.parse(a?.toString())}?.find{it}

    // Get max price valid from, from the lines
    maxLinePricingValidFrom = contractPricingDateMap?.get(key.split("\\|").getAt(0).trim())

    // Get Max Date between Quote DS Contract Pricing Date, Max Line Pricing Valid From, Actual New Contract Pricing Date and Current Contract Pricing Date.
    def maxDate = [
            lastValidFromNotOnQuote?.toString(),
            maxLinePricingValidFrom?.toString(),
            value.PriceValidFrom?.toString(),
            value.CurrentContractPricingDate?.toString()
    ].max()

    def maxDateWithoutChanges = [
            lastValidFromNotOnQuote?.toString(),
            value.PriceValidFrom?.toString(),
            value.CurrentContractPricingDate?.toString()
    ].max()

    def newContractPricing = prevContractPO?.NewPricingChanged && prevContractPO?.NewContractPricingDate ? prevContractPO?.NewContractPricingDate : maxDate
    // Builds Matrix data map
    data = [
            SAPContractNumber         : value.SAPContractNumber,
            SoldTo                    : value.SoldTo,
            ShipTo                    : value.ShipTo,
            ContractValidFrom         : value.ContractValidFrom,
            ContractValidTo           : prevContractPO?.ContractValidToChanged && prevContractPO.ContractValidTo ? prevContractPO.ContractValidTo : value.ContractValidTo,
            CurrentContractPricingDate: value.CurrentContractPricingDate,
            NewContractPricingDate    : newContractPricing,
            NewPricingChanged         : prevContractPO?.NewPricingChanged,
            ContractValidToChanged    : prevContractPO?.ContractValidToChanged
    ]

    if(newContractPricing?.toString() != value.CurrentContractPricingDate?.toString() || prevContractPO?.ContractValidTo?.toString() != value?.ContractValidTo?.toString()) {
        headerHasChangedMap.put(value.SAPContractNumber, true)
    } else {
        headerHasChangedMap.put(value.SAPContractNumber, false)
    }

    values.add([
            data: data,
            rowType: [
                    label: "Contract Data",
                    url  : headerConstants.CONTRACT_PO_MATRIX_CONFIGURATOR_URL
            ],
    ])
}

headerInputConfiguratorValues?.put(headerConstants.HEADER_HAS_CHANGED_ID, headerHasChangedMap)
headerInputConfiguratorValues?.put(headerConstants.CONTRACT_PO_ID, values)

// Re-write the configurator values even if the user doesn't step in the Header Tab (UI).
quoteProcessor.addOrUpdateInput(
        "ROOT", [
        "name" : headerConstants.INPUTS_NAME,
        "label": headerConstants.INPUTS_LABEL,
        "url"  : headerConstants.EXISTING_INPUTS_URL,
        "type" : InputType.INLINECONFIGURATOR,
        "value": headerInputConfiguratorValues,
])

// MM Change

return null

def findValue(useConfiguratorValue, name, lnProduct, configuratorValues) {
    final calculations = libs.QuoteLibrary.Calculations

    return useConfiguratorValue ? configuratorValues?.get(name) : calculations.getInputValue(lnProduct, name)
}

def trace(name, param, value) {
    if(api.isDebugMode()) {
        api.trace(name, param, value)
    } else {
        api.logWarn(name, value)
    }

}
