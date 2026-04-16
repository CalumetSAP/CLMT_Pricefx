if (api.isInputGenerationExecution()) return

api.local.product = api.product()
api.local.lineId = api.currentItem("lineId")
api.local.outputKey = out.HiddenInputs?.get("SAPContractNumber") + "|" + out.HiddenInputs?.get("SAPLineId")
