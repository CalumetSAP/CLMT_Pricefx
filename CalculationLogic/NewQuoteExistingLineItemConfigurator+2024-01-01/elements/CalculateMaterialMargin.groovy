if (api.isInputGenerationExecution() || !InputPrice?.entry?.getFirstInput()?.getValue() || !out.CalculateCost) return
def margin = InputPrice?.entry?.getFirstInput()?.getValue()?.toBigDecimal() - out.CalculateCost?.toBigDecimal()
def marginRounded = libs.QuoteLibrary.RoundingUtils.round(margin, 2)

InputMaterialMargin?.input?.setValue(marginRounded)

return null