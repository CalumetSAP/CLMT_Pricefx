import net.pricefx.common.api.InputType

if (api.isInputGenerationExecution()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def selectedContracts = InputContractNumber.input?.getValue()
def contractPO = InputContractPO.input?.getValue()?.data
def contracts = out.FindContractNumbers as Map

def headerHasChanged = false
def contractValue, tableValue
selectedContracts?.each { key ->
    contractValue = contracts?.get(key)
    tableValue = contractPO?.find {
        it.SAPContractNumber == contractValue?.SAPContractNumber &&
        it.SoldTo == contractValue?.SoldTo &&
        it.ShipTo == contractValue?.ShipTo
    }
    if (!contractValue || !tableValue) return

    def checks = [
            contractValue?.ContractValidTo?.toString() != tableValue?.ContractValidTo?.toString(),
            contractValue?.NewContractPricingDate?.toString() != tableValue?.NewContractPricingDate?.toString()
    ]

    def somethingHasChanged = checks.any { it }
    if (somethingHasChanged) headerHasChanged = true
}

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, headerConstants.HEADER_HAS_CHANGED_ID)
input = entry.getFirstInput()
input.setValue(headerHasChanged)

return entry