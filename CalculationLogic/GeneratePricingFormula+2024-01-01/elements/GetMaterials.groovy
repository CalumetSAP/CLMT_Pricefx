import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

Date today = getBusinessRulesTodayDate()
if (!today) {
    today = new Date()
}
api.local.today = today

List<String> materials = libs.PricelistLib.Query.getValidIndexQuotesRows(["Material"] as Set, today)?.collect()?.Material?.unique()

return materials

//This is to test an specific date
Date getBusinessRulesTodayDate () {
    String today = api.findLookupTableValues("BusinessRules", ["attribute1"], null, Filter.equal("name", "today"))?.find()?.attribute1
    if (today) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(today)
    }
    return null
}