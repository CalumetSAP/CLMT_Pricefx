def table = "VariancePricePoint"

def values =  [:]

api.findLookupTableValues(table).collect {
	values.put(it.key1+it.key2,it.attribute1)
}

def result = api.findLookupTableValues(table).collect{ it }
def uniqueValues = result.unique { it.key1+it.key2 }.findAll { it.attribute2 == 'Yes' }
def index = result.indexOf(uniqueValues.first())

java.util.Map.Entry selectedPricePoint = Acelerator.dropdown(
		label: "Price Point",
		values: values,
		defaultByIndex: index
)

//save the selected value in the global variable.
api.global.pricePointField = selectedPricePoint?.key
api.global.numericPricePoint = true
if (api.global.pricePointField && (api.global.pricePointField.endsWith("%") || api.global.pricePointField.endsWith("\$"))) {
	if(api.global.pricePointField.endsWith("%")){
		api.global.numericPricePoint = false
	}
	api.global.pricePointField = api.global.pricePointField[0..-2]
}
api.global.pricePointPercentBasisField = api.findLookupTableValues("VarianceParameters").find{
	it.name == "PercentBasisField"
}?.attribute1
api.global.pricePointLabel = selectedPricePoint?.value
