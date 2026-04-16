if (api.global.priceLetterDayAddition) return api.global.priceLetterDayAddition

def fields = ["name", "attribute2"]
api.global.priceLetterDayAddition = api.findLookupTableValues("BusinessRules", fields, null, Filter.equal("name", "PriceLetterDayAddition"))?.find()?.attribute2

return api.global.priceLetterDayAddition