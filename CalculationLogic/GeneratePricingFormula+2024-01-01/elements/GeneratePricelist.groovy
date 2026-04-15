import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

List<String> materials = out.GetMaterials

if (materials) {
    String today = new SimpleDateFormat("yyyy-MM-dd").format(api.local.today)
    def plType = api.find("PLPGTT", 0, 1, null, Filter.equal("uniqueName", libs.PricelistLib.Constants.PRICING_FORMULA_PL_TYPE))?.find()
    def calculationFormulaName = plType?.calculationFormulaName
    def body = [
            "data": [
                    "priceListName": "Pricing Formula ${today}",
                    "targetDate": today,
                    "errorMode": "ABORT",
                    "keepManualOverrides": null,
                    "useFilter": null,
                    "writeOnlyChangedItems": null,
                    "configuration": [
                            "shotgunModeEnabled": false,
                            "notifyWhenFinished": "NONE",
                            "plTypeTypeId": plType?.typedId,
                            "productFilterCriteria": [
                                    "_constructor": "AdvancedCriteria",
                                    "operator": "and",
                                    "criteria": [
                                            [
                                                    "fieldName": "sku",
                                                    "operator": "inSet",
                                                    "value": materials,
                                            ],
                                    ],
                            ],
                            "sortKeys": null,
                            "defaultFormulaOverride": calculationFormulaName,
                            "resultElementName": "NewPrice",
                            "matrixFormulaName": plType?.matrixFormulaName,
                            "matrixElementName": plType?.matrixFormulaElementName,
                            "uomOverrideElementName": null,
                            "elementNames": getElementNames(calculationFormulaName),
                            "hiddenElementNames": [],
                            "formulaParameters": [
                                    "Inputs": [
                                            "Hidden": null,
                                            "CalculationDateInput": today, //TODO put name into constant?
                                    ]
                            ],
                    ],
                    "override": null,
                    "nodeId": null,
            ]
    ]
    def response = api.boundCall("SystemUpdate", "/pricelistmanager.add", api.jsonEncode(body).toString(), false)
    api.trace("response", response)
}

def getElementNames(String defaultFormulaOverride) {
    def body = [
            "data": [
                    "configuration": [
                            "defaultFormulaOverride": defaultFormulaOverride
                    ]
            ]
    ]
    def response = api.boundCall("SystemUpdate", "/pricelistmanager.params", api.jsonEncode(body).toString(), false)
    if (response?.statusCode == "200") {
        return response?.responseBody?.response?.data?.find()?.formulaParameterReference?.elementName
    }
    api.throwException("Error trying to get 'Element Names' for the pricelist generation")
}
