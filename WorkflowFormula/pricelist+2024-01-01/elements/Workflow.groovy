if (api.isInputGenerationExecution()) return

workflow.withRunDefaultPostApprovalStepLogicOnEmptyWorkflow(true)
workflow.withDefaultPostApprovalStepLogic("DefaultPostStepLogic")

if (pricelist.headerTypeUniqueName == libs.PricelistLib.Constants.FREIGHT_MAINTENANCE_PL_TYPE || pricelist.headerTypeUniqueName == libs.PricelistLib.Constants.RAIL_FREIGHT_MAINTENANCE_PL_TYPE) {
    workflow.addApprovalStep("Condition Record Step")
            .withApprovers(api.user("loginName"))
            .withConditionRecordLogic("FreightPriceList_CR")
} else {
    workflow.addApprovalStep("Condition Record Step")
            .withApprovers(api.user("loginName"))
            .withConditionRecordLogic("PriceList_CR")
}