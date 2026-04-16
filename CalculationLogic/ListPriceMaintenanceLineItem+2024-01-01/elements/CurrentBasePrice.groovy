if (!out.IsPLSelected) return null

if (api.local.isNewProduct) return null

return api.local.zbplScalesMerged.ConditionRate ?: out.ZBPLMerged.Price