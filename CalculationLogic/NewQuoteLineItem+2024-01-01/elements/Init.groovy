api.local.isSoldToOnly
api.local.lineId = api.currentItem("lineId")

if (!api.isInputGenerationExecution()) {
    if (api.product("attribute6") != api.global.selectedDivision) api.addWarning("The selected material is in a different division than the selected customer")
}
