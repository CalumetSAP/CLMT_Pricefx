import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

import java.text.SimpleDateFormat

def sdf = new SimpleDateFormat("yyyy-MM-dd")

def entry = api.createConfiguratorEntry()

def indexNewValue = PFIndexNumber?.entry?.getFirstInput()?.getValue()
def referencePeriodNewValue = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
def adderNewValue = PFAdder?.entry?.getFirstInput()?.getValue()
def adderUOMNewValue = PFAdderUOM?.entry?.getFirstInput()?.getValue()
def priceNewValue = InputPrice?.entry?.getFirstInput()?.getValue()
def deliveredPriceNewValue = InputDeliveredPrice?.entry?.getFirstInput()?.getValue()
def pricingUOMNewValue = InputPricingUOM?.input?.getValue()
def freightAmountNewValue = InputFreightAmount?.entry?.getFirstInput()?.getValue()
//def priceTypeNewValue = InputPriceType?.input?.getValue()
def pricelistNewValue = InputPricelist?.input?.getValue()
def priceValidFromNewValue = InputPriceValidFrom?.input?.getValue()
def freightValidToNewValue = InputFreightValidTo?.input?.getValue()
def freightUOMNewValue = InputFreightUOM?.input?.getValue()
def recalculationDateNewValue = PFRecalculationDate?.input?.getValue()

// keep track on changes
ContextParameter indexPreviousValue = entry.createParameter(InputType.HIDDEN, PFIndexNumber?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter referencePeriodPreviousValue = entry.createParameter(InputType.HIDDEN, PFReferencePeriod?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter adderPreviousValue = entry.createParameter(InputType.HIDDEN, PFAdder?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter adderUOMPreviousValue = entry.createParameter(InputType.HIDDEN, PFAdderUOM?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter pricePreviousValue = entry.createParameter(InputType.HIDDEN, InputPrice?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter deliveredPricePreviousValue = entry.createParameter(InputType.HIDDEN, InputDeliveredPrice?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter pricingUOMPreviousValue = entry.createParameter(InputType.HIDDEN, InputPricingUOM?.input?.getName() + "Previous")
ContextParameter freightAmountPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightAmount?.entry?.getFirstInput()?.getName() + "Previous")
//ContextParameter priceTypePreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceType?.input?.getName() + "Previous")
ContextParameter pricelistPreviousValue = entry.createParameter(InputType.HIDDEN, InputPricelist?.input?.getName() + "Previous")
ContextParameter priceValidFromPreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceValidFrom?.input?.getName() + "Previous")
ContextParameter priceValidToPreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceValidTo?.input?.getName() + "Previous")
ContextParameter freightValidFromPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightValidFrom?.input?.getName() + "Previous")
ContextParameter freightValidToPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightValidTo?.input?.getName() + "Previous")
ContextParameter freightUOMPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightUOM?.input?.getName() + "Previous")
ContextParameter recalculationDatePreviousValue = entry.createParameter(InputType.HIDDEN, PFRecalculationDate?.input?.getName() + "Previous")

def callback = { v ->
    api.local.indexHasChanged = true
}

def adderCallback = { v ->
    api.local.adderHasChanged = true
}

def priceCallback = { v ->
    api.local.priceHasChanged = true
}

def deliveredPriceCallback = { v ->
    api.local.deliveredPriceHasChanged = true
    api.local.indexHasChanged = true
}

def pricingUOMCallback = { v ->
    api.local.indexHasChanged = true
}

def freightAmountCallback = { v ->
    api.local.freightAmountHasChanged = true
}

//def priceTypeCallback = { v ->
//    api.local.priceTypeHasChanged = true
//}

def pricelistCallback = { v ->
    api.local.pricelistHasChanged = true
}

def recalculationDateCallback = { v ->
    api.local.recalculationDateHasChanged = true
}

def priceValidCallback = { v ->
    InputFreightAmount?.entry?.getFirstInput()?.setValue(out.FindFreightValues?.FreightAmount?.toBigDecimal() ?: null)
    freightAmountPreviousValue?.setValue(out.FindFreightValues?.FreightAmount?.toBigDecimal() ?: null)
    InputFreightValidFrom?.input?.setValue(out.FindFreightValues?.FreightValidFrom)
    freightValidFromPreviousValue?.setValue(out.FindFreightValues?.FreightValidFrom)
    InputFreightValidTo?.input?.setValue(out.FindFreightValues?.FreightValidTo)
    freightValidToPreviousValue?.setValue(out.FindFreightValues?.FreightValidTo)
    api.local.priceValidFromHasChanged = true
}

def priceValidFromCallback = { v ->
    def newDate = v instanceof Date ? v : libs.QuoteLibrary.DateUtils.parseToDate(v)
    def newValidTo = libs.QuoteLibrary.DateUtils.sumDays(newDate, 365)
    InputPriceValidTo?.input?.setValue(newValidTo)
    priceValidToPreviousValue?.setValue(sdf.format(newValidTo))
}

def freightValidFromCallback = { v ->
    def newDate = v instanceof Date ? v : libs.QuoteLibrary.DateUtils.parseToDate(v)
    def newValidTo = libs.QuoteLibrary.DateUtils.sumDays(newDate, 365)
    InputFreightValidTo?.input?.setValue(newValidTo)
    freightValidToPreviousValue?.setValue(sdf.format(newValidTo))
    api.local.freightValidFromHasChanged = true
}

def freightUOMCallback = { v ->
    api.local.freightAmountHasChanged = true
}

if (indexNewValue != indexPreviousValue?.getValue()) {
    indexPreviousValue?.setValue(indexNewValue)
    callback(indexNewValue)
}

if (referencePeriodNewValue != referencePeriodPreviousValue?.getValue()) {
    referencePeriodPreviousValue?.setValue(referencePeriodNewValue)
    callback(referencePeriodNewValue)
}

if (adderNewValue != adderPreviousValue?.getValue()) {
    adderPreviousValue?.setValue(adderNewValue)
    adderCallback(adderNewValue)
}

if (adderUOMNewValue != adderUOMPreviousValue?.getValue()) {
    adderUOMPreviousValue?.setValue(adderUOMNewValue)
    callback(adderUOMNewValue)
}

if (priceNewValue != pricePreviousValue?.getValue()) {
    pricePreviousValue?.setValue(priceNewValue)
    priceCallback(priceNewValue)
}

if (deliveredPriceNewValue != deliveredPricePreviousValue?.getValue()) {
    deliveredPricePreviousValue?.setValue(deliveredPriceNewValue)
    deliveredPriceCallback(deliveredPriceNewValue)
}

if (pricingUOMNewValue != pricingUOMPreviousValue?.getValue()) {
    pricingUOMPreviousValue?.setValue(pricingUOMNewValue)
    pricingUOMCallback(pricingUOMNewValue)
}

if (freightAmountNewValue != freightAmountPreviousValue?.getValue()) {
    freightAmountPreviousValue?.setValue(freightAmountNewValue)
    freightAmountCallback(freightAmountNewValue)
}

//if (priceTypeNewValue != priceTypePreviousValue?.getValue()) {
//    priceTypePreviousValue?.setValue(priceTypeNewValue)
//    priceTypeCallback(priceTypeNewValue)
//}

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

freightValidFromNewValue = InputFreightValidFrom?.input?.getValue()
if (freightValidFromNewValue != freightValidFromPreviousValue?.getValue()) {
    if (freightValidFromNewValue && freightValidFromPreviousValue?.getValue()) freightValidFromCallback(freightValidFromNewValue)
    freightValidFromPreviousValue?.setValue(freightValidFromNewValue)
}

if (freightValidToNewValue != freightValidToPreviousValue?.getValue()) {
    freightValidToPreviousValue?.setValue(freightValidToNewValue)
}

if (freightUOMNewValue != freightUOMPreviousValue?.getValue()) {
    freightUOMPreviousValue?.setValue(freightUOMNewValue)
    freightUOMCallback(freightUOMNewValue)
}

if (recalculationDateNewValue != recalculationDatePreviousValue?.getValue()) {
    recalculationDatePreviousValue?.setValue(recalculationDateNewValue)
    recalculationDateCallback(recalculationDateNewValue)
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