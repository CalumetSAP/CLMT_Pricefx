import net.pricefx.common.api.CalculationResultType
import net.pricefx.common.api.FieldFormatType

Map<String, Object> create(String id, String label, String group) {
	return [
			"resultName" : id,
			"resultLabel": label,
			"resultType" : CalculationResultType.SIMPLE,
			"resultGroup": group
	]
}

Map<String, Object> numeric(String id, String label, String group, BigDecimal value) {
	def output = create(id, label, group)
	output["formatType"] = FieldFormatType.NUMERIC
	output["result"] = value
	return output
}

Map<String, Object> numeric(String id, String label, String group, BigDecimal value, String suffix) {
	def output = create(id, label, group)
	output["formatType"] = FieldFormatType.NUMERIC
	output["result"] = value
	output["suffix"] = suffix
	return output
}

Map<String, Object> text(String id, String label, String group, String value) {
	def output = create(id, label, group)
	output["formatType"] = FieldFormatType.TEXT
	output["result"] = value
	return output
}

Map<String, Object> text(String id, String label, String group, String value, Boolean isOverridable) {
	def output = create(id, label, group)
	output["formatType"] = FieldFormatType.TEXT
	output["result"] = value
	output["isOverridable"] = isOverridable
	return output
}

Map<String, Object> percent(String id, String label, String group, BigDecimal value) {
	def output = create(id, label, group)
	output["formatType"] = FieldFormatType.PERCENT
	output["result"] = value
	return output
}

Map<String, Object> money(String id, String label, String group, BigDecimal value) {
	def output = create(id, label, group)
	output["formatType"] = FieldFormatType.MONEY
	output["result"] = value
	return output
}

Map<String, Object> moneyWithCurrency(String id, String label, String group, BigDecimal value, String suffix) {
	def output = money(id, label, group, value)
	output["suffix"] = suffix
	output["result"] = value
	return output
}

Map<String, Object> moneyWithCurrency(String id, String label, String group, BigDecimal value, String suffix, String format) {
	def output = moneyWithCurrency(id, label, group, value, suffix)
	output["formatType"] = format
	return output
}

Map<String, Object> addWarningOnNull(Map<String, Object> output, String message) {
	if (!output) return null
	if (output["result"] == null)
		output["warnings"] = [message]
	return output
}

Map<String, Object> addUserGroup(Map<String, Object> output, String userGroups) {
	if (!output) return null
	output["userGroup"] = userGroups
	return output
}
