if (!quoteProcessor.isPostPhase()) return

String commandExecution = api.currentContext().commandName
if (commandExecution != "submit") return

quoteProcessor.updateField("creationWorkflowStatus", "Finished")