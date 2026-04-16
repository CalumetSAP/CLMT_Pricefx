import java.math.RoundingMode

BigDecimal round(BigDecimal number, int decimalPlaces) {
    if (number == null) {
        return null
    }
    return number.setScale(decimalPlaces, RoundingMode.HALF_UP)
}