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

return null