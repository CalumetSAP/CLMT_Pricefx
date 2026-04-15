def soldTos = api.local.allRows?.collect{it.SoldTo}?.unique()

def exclusions = libs.PricelistLib.Common.getExclusions(soldTos)

api.global.exclusions = exclusions

return exclusions