if (api.isInputGenerationExecution()) return

return api.global.approvalSequenceMap?.get(api.local.lineId) ?: api.global.outputsMap?.get(api.local.outputKey)?.get("ApprovalSequence")