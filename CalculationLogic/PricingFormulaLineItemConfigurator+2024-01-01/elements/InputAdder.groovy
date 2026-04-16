final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
final roundingUtils = libs.QuoteLibrary.RoundingUtils

def readOnly = api.local.readOnly ? true : false

def entry = libs.BdpLib.UserInputs.createInputDecimal(
        lineItemConstants.PF_CONFIGURATOR_ADDER_ID,
        lineItemConstants.PF_CONFIGURATOR_ADDER_LABEL,
        true,
        readOnly,
        "#,##0.0000",
)

entry?.getFirstInput()?.setValue(roundingUtils.round(entry?.getFirstInput()?.getValue()?.toBigDecimal(), 4))

return entry