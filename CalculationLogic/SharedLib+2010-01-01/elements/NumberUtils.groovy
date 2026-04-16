/**
 * Calculate percentage by dividing to 100. NULL value will return 0.0
 * @param : value. Number to calculate
 * @return : percentage value in BigDecimal
 * */
BigDecimal toPercentage(Object value) {
    return toBigDecimal(value, BigDecimal.ZERO, true) / BigDecimal.valueOf(100)
}

/**
 * Cast Number to BigDecimal
 * @param : value. Number to convert
 * @param : defaultValue.
 * @param : defaultOnException
 * @return : BigDecimal value of input
 * */
BigDecimal toBigDecimal(Object value, BigDecimal defaultValue = null, boolean defaultOnException = false) {
    try {
        return (value ?: defaultValue) as BigDecimal
    }
    catch (e) {
        if (defaultOnException) {
            return defaultValue
        }
        else {
            api.throwException(e.getMessage())
        }
    }
}
