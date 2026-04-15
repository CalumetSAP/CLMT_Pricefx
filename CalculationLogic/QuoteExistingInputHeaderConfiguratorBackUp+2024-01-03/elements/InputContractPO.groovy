if (api.isInputGenerationExecution()) return

final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator

def readOnlyColumns = ["SAPContractNumber", "SoldTo", "ShipTo", "ContractValidFrom", "CurrentContractPricingDate"]

def columns = [
        [name: 'SAPContractNumber', label: 'SAP Contract Number', type: 'TEXT'],
        [name: 'SoldTo', label: 'Sold To', type: 'TEXT'],
        [name: 'ShipTo', label: 'Ship To', type: 'TEXT'],
        [name: 'ContractValidFrom', label: 'Contract Valid From', type: 'DATE'],
        [name: 'ContractValidTo', label: 'Contract Valid To', type: 'DATE'],
        [name: 'ContractPO', label: 'Contract PO#', type: 'TEXT'],
        [name: 'CurrentContractPricingDate', label: 'Current Contract Pricing Date', type: 'DATE'],
        [name: 'NewContractPricingDate', label: 'New Contract Pricing Date', type: 'DATE'],
]

def ct = api.inputBuilderFactory()
        .createConfiguratorTable(headerConstants.CONTRACT_PO_ID)
        .setColumns(columns)
        .withEnableClientFilter(true)
        .withEnableEditActions(true)
        .withEnableDuplicateActions(false)
        .withEnableDeleteActions(false)
        .withRowTypes([])
        .setFixTableHeight(true)
        .setLabel(headerConstants.CONTRACT_PO_LABEL)
        .buildContextParameter()
ct.addParameterConfigEntry("readOnlyColumns", readOnlyColumns)
ct.addParameterConfigEntry("disableRowSelection", true)

def ce = api.createConfiguratorEntry()
ce.createParameter(ct)

input = ce.getFirstInput()

def contracts = out.FindContractNumbers as Map
def selectedContracts = InputContractNumber.input?.getValue()
def contractPricingDateMap = api.local.contractPricingDate ?: [:]

def previousValues = input.getValue()

def value, data, suggestedPricingDate, contractPricingDate
def values = []
selectedContracts?.each { key ->
    value = contracts?.get(key)
    if (!value) return
    def prevContractPO = previousValues?.data?.find {
        it.SAPContractNumber == value.SAPContractNumber &&
                it.SoldTo == value.SoldTo &&
                it.ShipTo == value.ShipTo
    }

    if (prevContractPO) value = prevContractPO

    suggestedPricingDate = contractPricingDateMap?.get(key.split("\\|").getAt(0).trim())

    contractPricingDate = suggestedPricingDate && suggestedPricingDate?.toString() >= value.NewContractPricingDate?.toString() ? suggestedPricingDate : value.NewContractPricingDate
    data = [
            SAPContractNumber         : value.SAPContractNumber,
            SoldTo                    : value.SoldTo,
            ShipTo                    : value.ShipTo,
            ContractValidFrom         : value.ContractValidFrom,
            ContractValidTo           : value.ContractValidTo,
            ContractPO                : value.ContractPO,
            CurrentContractPricingDate: value.CurrentContractPricingDate,
            NewContractPricingDate    : contractPricingDate
    ]
    values.add([
            rowType: [
                    label: "Contract Data",
                    url  : headerConstants.CONTRACT_PO_MATRIX_CONFIGURATOR_URL
            ],
            data   : data
    ])
}

input.setValue(values)

return null