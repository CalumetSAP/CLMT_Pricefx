def quote = api.currentItem()

return quote.get("inputs").find { it.name == "InputsConfigurator" }?.value?.ExternalNotesInput ?: ""