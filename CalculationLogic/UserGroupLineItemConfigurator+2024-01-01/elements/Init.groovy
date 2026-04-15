def user = api.user("loginName") as String
api.local.isPricingGroup = api.isUserInGroup("Pricing", user)
api.local.isFreightGroup = api.isUserInGroup("Freight", user)
api.local.isSalesGroup = api.isUserInGroup("Sales", user)