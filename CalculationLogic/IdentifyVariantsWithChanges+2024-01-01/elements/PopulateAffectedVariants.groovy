import java.text.SimpleDateFormat

if (api.isInputGenerationExecution()) return

def affectedSPSVariants = out.ProcessSPSVariants
def affectedPBVariants = out.ProcessPBVariants

addAffectedVariants(affectedSPSVariants, "SPS")
addAffectedVariants(affectedPBVariants, "PB")

return null

def addAffectedVariants(affectedVariants, dashboard) {
    def cptName = libs.QuoteConstantsLibrary.Tables.AFFECTED_VARIANTS
    def ppId = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()?.get("id")
    def sdf = new SimpleDateFormat("yyyy-MM-dd")
    def today = new Date()
    def split
    affectedVariants?.each { key ->
        split = key.split("\\|")
        buildRowToAddOrUpdate(ppId, split[0], dashboard, sdf.parse(split[1] as String), today)
    }
}

private def buildRowToAddOrUpdate(ppId, variant, dashboard, effectiveDate, today) {
    def req = [data: [
            key1: variant,
            key2: effectiveDate,
            key3: dashboard,
            key4: today,
            attribute1: false,
            attribute2: false,
            attribute3: api.uuid()
    ]]

    def body = api.jsonEncode(req)?.toString()

    def res = api.boundCall("SystemUpdate", "/lookuptablemanager.integrate/" + ppId, body, false)
}