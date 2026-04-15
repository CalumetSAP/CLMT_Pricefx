/**
 * Retrieves the percentage ratio for a given value and percentage base.
 * The value returned is between 0% - 100%
 * @param value value for the calculation
 * @param percentageBase percentage base for the calculation
 * @return percentage value calculated for the provided values
 */
BigDecimal getPercentageRatio(BigDecimal value, BigDecimal percentageBase) {
    return value != null && percentageBase ? 100 * value / percentageBase : null
}
