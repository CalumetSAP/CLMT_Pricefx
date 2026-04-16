def filters = [
        Filter.equal("code", "PL"),
        Filter.equal("workflowStatus", "SUBMITTED")
]

return api.stream("W", "lastUpdateDate", ["currentStepId"], *filters).withCloseable { it.collect().currentStepId }