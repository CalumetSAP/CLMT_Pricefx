import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

if (api.local.effectiveDateNewValue) {
    def effDate = sdf.parse(api.local.effectiveDateNewValue)
    Calendar expirationDate = Calendar.getInstance()
    expirationDate.setTime(effDate)
    expirationDate.add(Calendar.DAY_OF_YEAR, 365)

    out.ExpirationDate?.getFirstInput()?.setValue(expirationDate)
}