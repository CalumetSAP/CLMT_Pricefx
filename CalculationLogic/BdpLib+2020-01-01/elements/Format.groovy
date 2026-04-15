/**
 * @param number could be String | Decimal | BigNumber | null
 * @return default money format, two decimals, million separator by coma, prefixed with $
 * <pre>
 * Ex: -123.456 -> $ -1,234.46
 * </pre>
 */
String number(number) {
	return number == null
			? null
			: api.formatNumber(api.getLocale(), "#,##0.00;\$ -#,##0.00", number)
}

String money(value) {
	def formatted = number(value)
	return formatted != null
			? "\$ " + formatted
			: null
}

/**
 * @param number could be String | Decimal | BigNumber | null
 * @return default percent format, two decimals, prefixed with %
 * <pre>
 * Ex: -123.456 -> $ -1,234.46
 * </pre>
 */
String percent(number) {
	return number == null
			? null
			: api.formatNumber(api.getLocale(), "% ##.00;% -##.00", number)
}

String percentAfter(number) {
	return number == null
			? null
			: api.formatNumber(api.getLocale(), "##.00 %;-##.00 %", number)
}

String thousands(number) {
	return number && number != 0
			? api.formatNumber(api.getLocale(), "\$ #,##0 K;\$ -#,##0 K", number/1000)
			: null
}


String number(number, pattern) {
	return number == null
			? null
			: api.formatNumber(api.getLocale(), pattern, number)
}

String date(String date, String from, String to) {
	return api.parseDate(from, date).format(to)
}