if (api.isInputGenerationExecution()) return

return api.global.guardrailMap?.get(api.local.lineId)