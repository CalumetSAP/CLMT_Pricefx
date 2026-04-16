import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

if (api.local.effectiveDateNewValue || api.local.modeOfTransportationHasChanged) {
    def businessRules = api.isInputGenerationExecution() ? [:] : out.FindBusinessRules?.ExpirationDateDefaultByMode

    def effectiveDate = sdf.parse(out.InputEffectiveDate?.getFirstInput()?.getValue())
    def modeOfTransportation = out.InputModeOfTransportation?.getFirstInput()?.getValue()
    Calendar expirationDate = Calendar.getInstance()
    expirationDate.setTime(effectiveDate)
    expirationDate.add(Calendar.YEAR, 1)
    if (modeOfTransportation) {
        def defaultDate = businessRules[modeOfTransportation]
        if (defaultDate) {
            def parts = defaultDate.split('/')
            def month = parts[0] as int
            def day   = parts[1] as int

            expirationDate.set(Calendar.DAY_OF_MONTH, day)
            expirationDate.set(Calendar.MONTH, month - 1)
        }
    }

    out.InputExpirationDate?.getFirstInput()?.setValue(expirationDate)
}