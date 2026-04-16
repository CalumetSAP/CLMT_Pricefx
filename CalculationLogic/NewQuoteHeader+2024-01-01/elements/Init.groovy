api.local.loginName = api.user("loginName")

def qp = quoteProcessor
if (qp.isPrePhase()){
    qp.setRenderInfo("userGroupEdit", "hide", true)
    qp.setRenderInfo("userGroupViewDetails", "hide", true)
    qp.setRenderInfo("targetDate", "disabled", true)
//    qp.setRenderInfo("expiryDate", "disabled", true)
    qp.setRenderInfo("expiryDate", "disabled", false)
}