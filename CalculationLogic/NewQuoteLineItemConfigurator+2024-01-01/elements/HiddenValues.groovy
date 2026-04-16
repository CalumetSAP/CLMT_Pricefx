import net.pricefx.common.api.InputType
import net.pricefx.server.dto.calculation.ContextParameter

def entry = api.createConfiguratorEntry()

def indexNewValue = PFIndexNumber?.entry?.getFirstInput()?.getValue()
def referencePeriodNewValue = PFReferencePeriod?.entry?.getFirstInput()?.getValue()
def adderNewValue = PFAdder?.entry?.getFirstInput()?.getValue()
def adderUOMNewValue = PFAdderUOM?.entry?.getFirstInput()?.getValue()
def priceNewValue = InputPrice?.entry?.getFirstInput()?.getValue()
def deliveredPriceNewValue = InputDeliveredPrice?.entry?.getFirstInput()?.getValue()
def pricingUOMNewValue = InputPricingUOM?.input?.getValue()
def freightAmountNewValue = InputFreightAmount?.entry?.getFirstInput()?.getValue()
def priceTypeNewValue = InputPriceType?.input?.getValue()
def pricelistNewValue = InputPricelist?.input?.getValue()
def numberOfDecimalsNewValue = InputNumberOfDecimals?.input?.getValue()
def recalculationDateNewValue = PFRecalculationDate?.input?.getValue()
def priceValidFromNewValue = InputPriceValidFrom?.input?.getValue()
def freightUOMNewValue = InputFreightUOM?.input?.getValue()
def freightValidFromNewValue = InputFreightValidFrom?.input?.getValue()
def modeOfTransportationNewValue = InputModeOfTransportation?.input?.getValue()

// keep track on changes
ContextParameter indexPreviousValue = entry.createParameter(InputType.HIDDEN, PFIndexNumber?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter referencePeriodPreviousValue = entry.createParameter(InputType.HIDDEN, PFReferencePeriod?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter adderPreviousValue = entry.createParameter(InputType.HIDDEN, PFAdder?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter adderUOMPreviousValue = entry.createParameter(InputType.HIDDEN, PFAdderUOM?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter pricePreviousValue = entry.createParameter(InputType.HIDDEN, InputPrice?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter deliveredPricePreviousValue = entry.createParameter(InputType.HIDDEN, InputDeliveredPrice?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter pricingUOMPreviousValue = entry.createParameter(InputType.HIDDEN, InputPricingUOM?.input?.getName() + "Previous")
ContextParameter freightAmountPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightAmount?.entry?.getFirstInput()?.getName() + "Previous")
ContextParameter priceTypePreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceType?.input?.getName() + "Previous")
ContextParameter pricelistPreviousValue = entry.createParameter(InputType.HIDDEN, InputPricelist?.input?.getName() + "Previous")
ContextParameter numberOfDecimalsPreviousValue = entry.createParameter(InputType.HIDDEN, InputNumberOfDecimals?.input?.getName() + "Previous")
ContextParameter recalculationDatePreviousValue = entry.createParameter(InputType.HIDDEN, PFRecalculationDate?.input?.getName() + "Previous")
ContextParameter priceValidFromPreviousValue = entry.createParameter(InputType.HIDDEN, InputPriceValidFrom?.input?.getName() + "Previous")
ContextParameter freightUOMPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightUOM?.input?.getName() + "Previous")
ContextParameter freightValidFromPreviousValue = entry.createParameter(InputType.HIDDEN, InputFreightValidFrom?.input?.getName() + "Previous")
ContextParameter modeOfTransportationPreviousValue = entry.createParameter(InputType.HIDDEN, InputModeOfTransportation?.input?.getName() + "Previous")

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

def freightUOMCallback = { v ->
    InputFreightUOM?.input?.setValue(v)
}

def freightAmountCallback = { v ->
    api.local.freightAmountHasChanged = true
}

def priceTypeCallback = { v ->
    api.local.priceTypeHasChanged = true
}

def pricelistCallback = { v ->
    api.local.pricelistHasChanged = true
}

def numberOfDecimalsFromCallback = { v ->
    api.local.numberOfDecimalsHasChanged = true
}

def recalculationDateCallback = { v ->
    api.local.recalculationDateHasChanged = true
}

def priceValidFromCallback = { v ->
    api.local.priceValidFromHasChanged = true
}

def freightUOMChangeCallback = { v ->
    api.local.freightAmountHasChanged = true
}

def freightValidFromChangeCallback = { v ->
    api.local.freightValidFromHasChanged = true
}

def modeOfTransportationChangeCallback = { v ->
    api.local.modeOfTransportationHasChanged = true
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
    if (priceTypeNewValue != "4") {
        freightUOMCallback(pricingUOMNewValue)
    }
}

if (freightAmountNewValue != freightAmountPreviousValue?.getValue()) {
    freightAmountPreviousValue?.setValue(freightAmountNewValue)
    freightAmountCallback(freightAmountNewValue)
}

if (priceTypeNewValue != priceTypePreviousValue?.getValue()) {
    priceTypePreviousValue?.setValue(priceTypeNewValue)
    priceTypeCallback(priceTypeNewValue)
}

if (pricelistNewValue != pricelistPreviousValue?.getValue()) {
    pricelistPreviousValue?.setValue(pricelistNewValue)
    pricelistCallback(pricelistNewValue)
}

if (numberOfDecimalsNewValue != numberOfDecimalsPreviousValue?.getValue()) {
    numberOfDecimalsPreviousValue?.setValue(numberOfDecimalsNewValue)
    if (numberOfDecimalsNewValue && numberOfDecimalsPreviousValue?.getValue()) numberOfDecimalsFromCallback(numberOfDecimalsNewValue)
}

if (recalculationDateNewValue != recalculationDatePreviousValue?.getValue()) {
    recalculationDatePreviousValue?.setValue(recalculationDateNewValue)
    recalculationDateCallback(recalculationDateNewValue)
}

if (priceValidFromNewValue != priceValidFromPreviousValue?.getValue()) {
    priceValidFromPreviousValue?.setValue(priceValidFromNewValue)
    priceValidFromCallback(priceValidFromNewValue)
}

if (freightUOMNewValue != freightUOMPreviousValue?.getValue()) {
    freightUOMPreviousValue?.setValue(freightUOMNewValue)
    freightUOMChangeCallback(freightUOMNewValue)
}

if (freightValidFromNewValue != freightValidFromPreviousValue?.getValue()) {
    freightValidFromPreviousValue?.setValue(freightValidFromNewValue)
    freightValidFromChangeCallback(freightValidFromNewValue)
}

if (modeOfTransportationNewValue != modeOfTransportationPreviousValue?.getValue()) {
    modeOfTransportationPreviousValue?.setValue(modeOfTransportationNewValue)
    modeOfTransportationChangeCallback(modeOfTransportationNewValue)
}

return entry