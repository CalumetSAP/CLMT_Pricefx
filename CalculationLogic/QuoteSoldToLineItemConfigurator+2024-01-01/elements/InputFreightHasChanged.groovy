import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def inputValues = InputShipToMatrix.input.getValue() ?: []

def hasChanged = inputValues?.any { it[soldToConstants.MATRIX_FREIGHT_HAS_CHANGED_ID_HIDDEN_ID] } || inputValues?.any { it[soldToConstants.MATRIX_REMOVE_ID] == "Yes" }

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.FREIGHT_HAS_CHANGED_ID)
entry.getFirstInput().setValue(hasChanged)

return entry