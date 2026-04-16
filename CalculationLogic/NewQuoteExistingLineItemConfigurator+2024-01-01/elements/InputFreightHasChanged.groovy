import net.pricefx.common.api.InputType

import java.text.SimpleDateFormat

final lineItemConstants = libs.QuoteConstantsLibrary.LineItem

def entry = api.createConfiguratorEntry()
entry.createParameter(InputType.HIDDEN, lineItemConstants.FREIGHT_HAS_CHANGED_ID)

def checks = [
        InputFreightAmount?.entry?.getFirstInput()?.getValue() != out.FindFreightValues?.FreightAmount?.toBigDecimal(),
        compareDates(InputFreightValidFrom?.input?.getValue()?.toString(), api.local.defaultFreightValidFrom?.toString()),
        compareDates(InputFreightValidTo?.input?.getValue()?.toString(), api.local.defaultFreightValidTo?.toString()),
        InputFreightUOM?.input?.getValue() != out.FindFreightValues?.FreightUOM,
]

def hasChanged = checks.any { it } && InputFreightAmount?.entry?.getFirstInput()?.getValue()

entry.getFirstInput().setValue(hasChanged)

return entry

def compareDates(oneDate, otherDate) {
    if ((oneDate && !otherDate) || (!oneDate && otherDate)) return true
    if (!oneDate && !otherDate) return false
    def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    def date1 = dateFormat.parse(oneDate)
    def date2 = dateFormat.parse(otherDate)

    return date1.before(date2) || date1.after(date2)
}