import net.pricefx.common.api.InputType

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def columnIds = ["ScaleQty", "ScaleUOM", "Price", "PriceUOM"]
def columnLabels = ["Scale Qty", "Scale UOM", "Price", "Price UOM"]
def readOnlyColumns = []
if (api.local.priceUOM) readOnlyColumns.add("PriceUOM")
def columnValueOptions = ["ScaleUOM": api.local.UOM ?: [], "PriceUOM": api.local.UOM ?: []]
def columnTypes = [InputType.INTEGERUSERENTRY, InputType.OPTION, InputType.USERENTRY, InputType.OPTION]
def defaults = [["ScaleQty": null,"ScaleUOM": api.local.MOQUOM, "Price": null, "PriceUOM": api.local.UOM]]

input = libs.BdpLib.UserInputs.createInputMatrix(
        lineItemConstants.SCALES_ID,
        lineItemConstants.SCALES_LABEL,
        columnIds,
        columnLabels,
        readOnlyColumns,
        columnValueOptions,
        columnTypes,
        true,
        false,
        false,
        false,
        true,
        defaults,
).getFirstInput()

return null