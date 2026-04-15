final general = libs.QuoteConstantsLibrary.General

//api.local.description = api.product("label", api.local.sku as String)
//api.local.unitOfMeasure = api.product("unitOfMeasure", api.local.sku as String)
//api.local.attribute9 = api.product("Attribute 9", api.local.sku as String)

// User Groups
def user = api.user("loginName") as String
api.local.isSalesGroup = api.isUserInGroup(general.USER_GROUP_SALES, user)
api.local.isPricingGroup = api.isUserInGroup(general.USER_GROUP_PRICING, user)
api.local.isFreightGroup = api.isUserInGroup(general.USER_GROUP_FREIGHT, user)
api.local.isApproverGroup = api.isUserInGroup(general.USER_GROUP_APPROVER, user)