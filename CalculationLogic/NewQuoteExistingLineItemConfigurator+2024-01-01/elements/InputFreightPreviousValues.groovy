import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.FREIGHT_PREVIOUS_VALUES_ID)

def map = [
        FreightAmount   : out.FindFreightValues?.FreightAmount?.toBigDecimal(),
        FreightValidFrom: out.FindFreightValues?.FreightValidFrom,
        FreightValidTo  : out.FindFreightValues?.FreightValidTo,
        FreightUOM      : out.FindFreightValues?.FreightUOM,
]

entry.getFirstInput().setValue(map)

return entry