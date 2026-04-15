final general = libs.QuoteConstantsLibrary.General

def user = api.user("loginName") as String
api.local.isNotFreightGroup = api.isUserInGroup(general.USER_GROUP_SALES, user) || api.isUserInGroup(general.USER_GROUP_PRICING, user)