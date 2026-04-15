/**
 * Update Pricelist create input configurator (pl.configuration.formulaParameters.Inputs)
 * <p>
 * Note: This method execute a bound call for that reason should be used only one time, for update multiple fields see updatePricelistConfiguration.
 * <p>Throws exception</p>
 * </p>
 *
 * <blockquote>
 * <pre>{@code
 * boolean boolean isUpdated = boundCall.updatePricelistCreateInput("1161", "StartDate", "2023-01-23")
 *}</pre>
 * </blockquote>
 *
 * @param pricelistId : Integer or String without '.PL'
 * @param inputId : input String id
 * @param value : any value
 * @return boolean true if success
 */
boolean updatePricelistCreateInput(String boundPartition, pricelistId, inputId, value) {
	if (!pricelistId) return false
	def pl = api.find("PL", 0, 1, null, Filter.equal("id", pricelistId)).find()
	def config = api.jsonDecode(pl.configuration)

	config.formulaParameters.Inputs[inputId] = value


	pl["typedId"] = "${pricelistId}.PL"
	def obj = [
			data            : [
					configuration: api.jsonEncode(config),
					"typedId"    : "${pricelistId}.PL",
					version      : pl.version
			],
			"operationType" : "update",
			"textMatchStyle": "exact"
	]

	try {
		def result = api.boundCall(boundPartition, "/update/PL", api.jsonEncode(obj))

		if (result.errorCode) {
			api.logInfo("Fail to update Pricelist(${pricelistId}) create inputs:", result.errorCode)
			return false
		}
		return true
	} catch (e) {
		api.logInfo("Fatal BdpLib/BoundCall/updatePricelistCreateInputs: exception on pricelist", pricelistId)
		api.logInfo("Fatal BdpLib/BoundCall/calculatePricelist: Bound Partition", boundPartition)
		api.logInfo("Fatal BdpLib/BoundCall/updatePricelistCreateInputs: ", e.getMessage())
		api.throwException("Fatal BdpLib/BoundCall/updatePricelistCreateInputs: exception on update ${pricelistId}." as String)
		return false
	}
}

/**
 * Update Pricelist Header input configurator (pl.configuration.headerInputs[0].value)
 * <p>
 * Note: This method execute a bound call for that reason should be used only one time, for update multiple fields see updatePricelistConfiguration.
 * <p>Throws exception</p>
 * </p>
 *
 * <blockquote>
 * <pre>{@code
 * boolean isUpdated = boundCall.updatePricelistHeaderInput("1161", "StartDate", "2023-01-23")
 *}</pre>
 * </blockquote>
 *
 * @param pricelistId Integer or String without '.PL'
 * @param inputId input String id
 * @param value any value
 * @return boolean true if success
 */
boolean updatePricelistHeaderInput(String boundPartition, pricelistId, inputId, value) {
	if (!pricelistId) return false
	def pl = api.find("PL", 0, 1, null, Filter.equal("id", pricelistId)).find()
	def config = api.jsonDecode(pl.configuration)

	config.headerInputs.find().value[inputId] = value

	pl["typedId"] = "${pricelistId}.PL"
	def obj = [
			data            : [
					configuration: api.jsonEncode(config),
					"typedId"    : "${pricelistId}.PL",
					version      : pl.version
			],
			"operationType" : "update",
			"textMatchStyle": "exact"
	]

	try {
		def result = api.boundCall(boundPartition, "/update/PL", api.jsonEncode(obj) as String)

		if (result.errorCode) {
			api.logInfo("Fail to update Pricelist(${pricelistId}) create inputs:", result.errorCode)
			return false
		}
		return true
	} catch (e) {
		api.logInfo("Fatal BdpLib/BoundCall/updatePricelistHeaderInputs: exception on pricelist", pricelistId)
		api.logInfo("Fatal BdpLib/BoundCall/calculatePricelist: Bound Partition", boundPartition)
		api.logInfo("Fatal BdpLib/BoundCall/updatePricelistHeaderInputs: ", e.getMessage())
		api.throwException("Fatal BdpLib/BoundCall/updatePricelistHeaderInputs: exception on update ${pricelistId}." as String)
		return false
	}
}

/**
 * Update Pricelist multiple fields: create input configurator and/or Header input configurator (pl.configuration)
 *
 * <p>Throws exception</p>
 *
 * <blockquote>
 * <pre>{@code
 * boolean updated = boundCall.updatePricelistConfiguration(1161, [
 * createInputs: ["StartDate": "2023-01-23", "EndDate": "2023-02-15"],
 * headerInputs: ["startDate": "2023-01-23"]
 * ])
 *}</pre>
 * </blockquote>
 *
 * @param pricelistId : Integer or String without '.PL'
 * @param values Map< String, Map< key, value>>: It should contain createInputs and/or headerInputs root keys
 * @return boolean true if success
 */
boolean updatePricelistConfiguration(String boundPartition, pricelistId, values) {
	if (!pricelistId) return false

	def pl = api.find("PL", 0, 1, null, Filter.equal("id", pricelistId)).find()
	if (!pl) api.throwException("Pricelist ${pricelistId} not found.")
	pl["typedId"] = "${pricelistId}.PL"

	def config = api.jsonDecode(pl.configuration)

	def createFieldsToUpdate = values["createInputs"]
	for (field in createFieldsToUpdate) config.formulaParameters.Inputs[field.key] = field.value

	def headerFieldsToUpdate = values["headerInputs"]
	for (field in headerFieldsToUpdate) config.headerInputs.find().value[field.key] = field.value

	def obj = [
			data            : [
					configuration: api.jsonEncode(config),
					"typedId"    : "${pricelistId}.PL",
					version      : pl.version
			],
			"operationType" : "update",
			"textMatchStyle": "exact"
	]

	try {
		def result = api.boundCall(boundPartition, "/update/PL", api.jsonEncode(obj) as String)

		if (result.errorCode) {
			api.logInfo("Fail to update Pricelist(${pricelistId}) create inputs:", result.errorCode)
			return false
		}
		return true
	} catch (e) {
		api.logInfo("Fatal BdpLib/BoundCall/updatePricelistConfiguration: exception on pricelist", pricelistId)
		api.logInfo("Fatal BdpLib/BoundCall/calculatePricelist: Bound Partition", boundPartition)
		api.logInfo("Fatal BdpLib/BoundCall/updatePricelistConfiguration: ", e.getMessage())
		api.throwException("Fatal BdpLib/BoundCall/updatePricelistConfiguration: exception on update ${pricelistId}." as String)
		return false
	}
}

boolean calculatePricelist(String boundPartition, pricelistId) {
	def obj = [data: [fullListRecalc: true]]

	try {
		def result = api.boundCall(boundPartition, "/pricelistmanager.calculate/${pricelistId}", api.jsonEncode(obj) as String)

		if (result.errorCode) {
			api.logInfo("Fail to update Pricelist(${pricelistId}) create inputs:", result.errorCode)
			return false
		}
		return true
	} catch (e) {
		api.logInfo("Fatal BdpLib/BoundCall/calculatePricelist: exception on pricelist", pricelistId)
		api.logInfo("Fatal BdpLib/BoundCall/calculatePricelist: Bound Partition", boundPartition)
		api.logInfo("Fatal BdpLib/BoundCall/calculatePricelist: ", e.getMessage())
		api.throwException("Fatal BdpLib/BoundCall/calculatePricelist: exception on update ${pricelistId}." as String)
		return false
	}
}
