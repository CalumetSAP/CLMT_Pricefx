import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

def havePermissions = api.local.isPricingGroup || api.local.isFreightGroup
def shouldShow = InputIncoterm?.input?.getValue() != "FCA" && InputFreightEstimate?.input?.getValue()

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def freightValidFrom = InputFreightValidFrom?.input?.getValue()
def modeOfTransportation = InputModeOfTransportation?.input?.getValue()

def required = api.local.isPricingGroup && shouldShow
def readOnly = !api.local.isPricingGroup && !shouldShow
def defaultValue = out.FindFreightValues?.FreightValidTo ?: getDefaultValue(freightValidFrom, modeOfTransportation, api.local.validToDate)
api.local.defaultFreightValidTo = defaultValue
def entry = null
if (havePermissions) {
    input = libs.BdpLib.UserInputs.createInputDate(
            lineItemConstants.FREIGHT_VALID_TO_ID,
            lineItemConstants.FREIGHT_VALID_TO_LABEL,
            required,
            readOnly,
            defaultValue
    ).getFirstInput()
} else {
    entry = libs.BdpLib.UserInputs.createInput(
            lineItemConstants.FREIGHT_VALID_TO_ID,
            InputType.HIDDEN,
            lineItemConstants.FREIGHT_VALID_TO_LABEL,
            false,
            true
    )
    input = entry.getFirstInput()
    if (!shouldShow) input?.setValue(null)
}

return entry

def getDefaultValue(String freightValidFrom, modeOfTransportation, defaultDate) {
    if (!freightValidFrom || !modeOfTransportation) return defaultDate

    def sdf = new SimpleDateFormat("yyyy-MM-dd")
    def date = sdf.parse(freightValidFrom)

    Calendar cal = Calendar.getInstance()
    cal.setTime(date)
    cal.add(Calendar.YEAR, 1)
    cal.set(Calendar.DAY_OF_MONTH, 15)

    if (modeOfTransportation == "BL") {
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        return sdf.format(cal.getTime())
    } else if (modeOfTransportation == "R0") {
        cal.set(Calendar.MONTH, Calendar.MARCH)
        return sdf.format(cal.getTime())
    } else {
        return defaultDate
    }
}