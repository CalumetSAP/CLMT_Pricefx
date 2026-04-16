if (api.isInputGenerationExecution()) return

final roundingUtils = libs.QuoteLibrary.RoundingUtils

// Update Recommended Price
def recommendedPrice = out.CalculateRecommendedPrice && !api.isInputGenerationExecution() ? out.CalculateRecommendedPrice as BigDecimal : null
InputRecommendedPrice?.input?.setValue(recommendedPrice)

// Update Cost
def cost = out.CalculateCost && !api.isInputGenerationExecution() ? out.CalculateCost : null
def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"

cost = roundingUtils.round(cost?.toBigDecimal(), numberOfDecimals?.toInteger())
InputCost?.input?.setValue(roundingUtils.round(cost?.toBigDecimal(), numberOfDecimals?.toInteger()).toString())

// Stop using default values
out.ShouldUseDefaultValues.getFirstInput().setValue(false)

return null