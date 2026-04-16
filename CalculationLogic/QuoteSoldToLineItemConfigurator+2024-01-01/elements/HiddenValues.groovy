import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

import java.text.SimpleDateFormat

def sdf = new SimpleDateFormat("yyyy-MM-dd")

def entry = api.createConfiguratorEntry()

def priceNewValue = InputPrice?.entry?.getFirstInput()?.getValue()
def pricingUOMNewValue = InputPricingUOM?.input?.getValue()
def pricelistNewValue = InputPricelist?.input?.getValue()
def priceValidFromNewValue = InputPriceValidFrom?.input?.getValue()

// keep track on changes
ContextParameter pricePreviousValue = entry.createParameter(InputType.HIDDEN, InputPrice?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter pricingUOMPreviousValue = entry.createParameter(InputType.HIDDEN, InputPricingUOM?.input?.getName() + "Previous")
ContextParameter pricelistPreviousValue = entry.createParameter(InputType.HIDDEN, InputPricelist?.input?.getName() + "Previous")
ContextParameter priceValidFromPreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceValidFrom?.input?.getName() + "Previous")
ContextParameter priceValidToPreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceValidTo?.input?.getName() + "Previous")

def priceCallback = { v ->
    api.local.priceHasChanged = true
}

def pricingUOMCallback = { v ->
    api.local.indexHasChanged = true
}

def pricelistCallback = { v ->
    api.local.pricelistHasChanged = true
}

def priceValidCallback = { v ->
    api.local.priceValidFromHasChanged = true
}

def priceValidFromCallback = { v ->
    def newDate = v instanceof Date ? v : libs.QuoteLibrary.DateUtils.parseToDate(v)
    def newValidTo = libs.QuoteLibrary.DateUtils.sumDays(newDate, 365)
    InputPriceValidTo?.input?.setValue(newValidTo)
    priceValidToPreviousValue?.setValue(sdf.format(newValidTo))
}

if (priceNewValue != pricePreviousValue?.getValue()) {
    pricePreviousValue?.setValue(priceNewValue)
    priceCallback(priceNewValue)
}

if (pricingUOMNewValue != pricingUOMPreviousValue?.getValue()) {
    pricingUOMPreviousValue?.setValue(pricingUOMNewValue)
    pricingUOMCallback(pricingUOMNewValue)
}

if (pricelistNewValue != pricelistPreviousValue?.getValue()) {
    pricelistPreviousValue?.setValue(pricelistNewValue)
    pricelistCallback(pricelistNewValue)
}

if (compareDates(priceValidFromNewValue?.toString(), priceValidFromPreviousValue?.getValue()?.toString())) {
    if (priceValidFromNewValue && priceValidFromPreviousValue?.getValue()) priceValidFromCallback(priceValidFromNewValue)
    priceValidFromPreviousValue?.setValue(priceValidFromNewValue)
    priceValidCallback(priceValidFromNewValue)
}

priceValidToNewValue = InputPriceValidTo?.input?.getValue()
if (priceValidToNewValue != priceValidToPreviousValue?.getValue()) {
    priceValidToPreviousValue?.setValue(priceValidToNewValue)
    priceValidCallback(priceValidToNewValue)
}

return entry

def compareDates(oneDate, otherDate) {
    if ((oneDate && !otherDate) || (!oneDate && otherDate)) return true
    if (!oneDate && !otherDate) return false
    def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    def date1 = dateFormat.parse(oneDate)
    def date2 = dateFormat.parse(otherDate)

    return date1.before(date2) || date1.after(date2)
}