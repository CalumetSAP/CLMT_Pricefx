if (api.isInputGenerationExecution()) return

libs.QuoteLibrary.Emails.defineEmailSubjectAndBody(quote, workflowHistory, approvable)