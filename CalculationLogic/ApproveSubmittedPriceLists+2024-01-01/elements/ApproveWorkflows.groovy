out.FindPLWorkflows?.each { wfCurrentStepId ->
    api.boundCall("SystemUpdate", "/workflowsmanager.approve/${wfCurrentStepId}", "", true)
}