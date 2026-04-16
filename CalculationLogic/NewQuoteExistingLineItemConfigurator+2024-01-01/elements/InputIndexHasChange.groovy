import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.INDEX_DATA_HAS_CHANGED_ID)

def checks = [
        api.local.contractData?.ReferencePeriod != PFReferencePeriod?.entry?.getFirstInput()?.getValue(),
        api.local.contractData?.IndexNumberOne != api.local.index1,
        api.local.contractData?.IndexNumberTwo != api.local.index2,
        api.local.contractData?.IndexNumberThree != api.local.index3,
        (api.local.contractData?.Adder != PFAdder?.entry?.getFirstInput()?.getValue() && out.PriceCompletedHidden?.getFirstInput()?.getValue() == lineItemConstants.PRICE_COMPLETED_ADDER_ID),
        api.local.contractData?.AdderUOM != PFAdderUOM?.entry?.getFirstInput()?.getValue(),
        api.local.contractData?.RecalculationDate?.toString() != PFRecalculationDate?.input?.getValue()?.toString(),
        api.local.contractData?.RecalculationPeriod != PFRecalculationPeriod?.input?.getValue()
]

def hasChanged = checks.any { it }

entry.getFirstInput().setValue(hasChanged)

return entry