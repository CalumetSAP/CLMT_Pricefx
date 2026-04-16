if (api.isInputGenerationExecution()) return null

def quote = api.currentItem()

def notDraftStatus = ["OFFER", "DEAL"]

return !notDraftStatus?.contains(quote?.quoteStatus) ? "Draft" : ""