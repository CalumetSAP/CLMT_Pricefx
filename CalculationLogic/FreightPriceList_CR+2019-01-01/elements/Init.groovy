def pl = api.currentItem()

api.local.plItems = libs.PricelistLib.Common.getAllPLItems(pl?.id)

return null