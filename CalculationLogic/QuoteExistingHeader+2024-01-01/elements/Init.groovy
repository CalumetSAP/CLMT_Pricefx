def qp = quoteProcessor
if (qp.isPrePhase()){
    qp.setRenderInfo("userGroupEdit","hide",true)
    qp.updateField("userGroupEdit", "Pricing,Freight,SalesManager")
    qp.setRenderInfo("userGroupViewDetails","hide",true)
    qp.updateField("userGroupViewDetails", "Pricing,Freight,SalesManager")
    qp.setRenderInfo("targetDate", "hide", true)
    qp.setRenderInfo("expiryDate", "hide", true)
    qp.setRenderInfo("externalRef", "hide", true)
}
api.local.loginName = api.user("loginName")