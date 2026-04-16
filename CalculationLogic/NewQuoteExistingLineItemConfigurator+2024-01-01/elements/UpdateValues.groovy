import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

final roundingUtils = libs.QuoteLibrary.RoundingUtils

def havePermissions = api.local.isPricingGroup
def priceType = InputPriceType?.input?.getValue()

// Update Recommended Price
def recommendedPrice = out.CalculateRecommendedPrice && !api.isInputGenerationExecution() ? out.CalculateRecommendedPrice as BigDecimal : null

InputRecommendedPrice?.input?.setValue(recommendedPrice)

if (priceType == "3" && havePermissions) {
    InputPrice?.entry?.getFirstInput()?.setValue(recommendedPrice)
    out.HiddenValues?.getInputs()?.find { it.name == InputPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(recommendedPrice)

    def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"
    def result = recommendedPrice != null ? libs.QuoteLibrary.RoundingUtils.round(recommendedPrice, numberOfDecimals?.toInteger()) : null

    InputDeliveredPrice?.entry?.getFirstInput()?.setValue(result)
    out.HiddenValues?.getInputs()?.find { it.name == InputDeliveredPrice?.entry?.getFirstInput()?.getName() + "Previous"}?.setValue(result)
}

// Update Cost
def cost = out.CalculateCost && !api.isInputGenerationExecution() ? out.CalculateCost : null
def numberOfDecimals = InputNumberOfDecimals?.input?.getValue() ?: "2"

cost = roundingUtils.round(cost?.toBigDecimal(), numberOfDecimals?.toInteger())
def inputCost = cost != null ? cost.toString() : ""
InputCost?.input?.setValue(roundingUtils.round(cost?.toBigDecimal(), numberOfDecimals?.toInteger()).toString())

//Update FreightValidTo
if (api.local.freightValidFromHasChanged) {
    def freightValidFrom = InputFreightValidFrom?.input?.getValue()?.toString()
    def modeOfTransportation = InputModeOfTransportation?.input?.getValue()

    def newValidTo = getNewValidTo(freightValidFrom, modeOfTransportation)

    if (newValidTo) InputFreightValidTo?.input?.setValue(newValidTo)
}

// Stop using default values
out.ShouldUseDefaultValues.getFirstInput().setValue(false)

return null

def getNewValidTo(String freightValidFrom, modeOfTransportation) {
    if (!freightValidFrom || !modeOfTransportation) return null

    def sdf = new SimpleDateFormat("yyyy-MM-dd")
    def date = sdf.parse(freightValidFrom)

    Calendar cal = Calendar.getInstance()
    cal.setTime(date)
    cal.add(Calendar.YEAR, 1)
    cal.set(Calendar.DAY_OF_MONTH, 15)

    if (modeOfTransportation == "BL") {
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        return cal.getTime()
    } else if (modeOfTransportation == "R0") {
        cal.set(Calendar.MONTH, Calendar.MARCH)
        return cal.getTime()
    } else {
        return null
    }
}