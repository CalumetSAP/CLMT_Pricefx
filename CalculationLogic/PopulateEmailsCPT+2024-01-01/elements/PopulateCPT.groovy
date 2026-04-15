if (api.isInputGenerationExecution()) return

def data = api.global.DSData
def pricingEmails = api.global.pricingEmails
def pendingEmails = api.global.pendingEmails

def pricingEmail, salesEmails, customEventJSON
pendingEmails?.each { key, values ->
    pricingEmail = pricingEmails[key]
    values.each {
        salesEmails = data[it.UUID as String]?.collect { it.SalesPersonEmail }?.findAll()
        customEventJSON = api.jsonEncode(["UUID": it.UUID, "Process": "DashboardEmail", "email": pricingEmail])
        if (salesEmails) populateSalesEmailsIntoCPT(it.UUID as String, salesEmails, key, pricingEmail, customEventJSON)

        updateEmailCreated(it.Variant, it.EffectiveDate, it.Dashboard, it.ChangeDate, it.DataSourceLoaded, it.UUID)
    }
}

def updateEmailCreated(variant, effectiveDate, dashboard, changeDate, dataSourceLoaded, uuid) {
    def cptName = "AffectedVariants"
    def cptData = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()
    api.addOrUpdate("MLTV4", buildRowToAddOrUpdate(cptData, variant, effectiveDate, dashboard, changeDate, dataSourceLoaded, uuid))
}

def populateSalesEmailsIntoCPT(String uuid, List salesEmails, template, pricingEmail, customEventJSON) {
    def cptName = "SendEmailsByUUID"
    def cptData = api.find("LT", 0, 1, null,["id"], Filter.equal("name", cptName))?.first()
    api.addOrUpdate("MLTV2", buildRowToAddOrUpdate(cptData, uuid, pricingEmail, template, customEventJSON))
    salesEmails.each { email ->
        api.addOrUpdate("MLTV2", buildRowToAddOrUpdate(cptData, uuid, email, template, customEventJSON))
    }
}

def buildRowToAddOrUpdate(cptData, variant, effectiveDate, dashboard, changeDate, dataSourceLoaded, uuid) {
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "key1"              : variant,
            "key2"              : effectiveDate,
            "key3"              : dashboard,
            "key4"              : changeDate,
            "attribute1"        : dataSourceLoaded,
            "attribute2"        : true,
            "attribute3"        : uuid,
    ]

    return ppRow
}

def buildRowToAddOrUpdate(cptData, uuid, email, template, customEventJSON) {
    def ppRow = [
            "lookupTableId"     : cptData.id,
            "lookupTableName"   : cptData.uniqueName,
            "key1"              : uuid,
            "key2"              : email,
            "attribute1"        : template,
            "attribute2"        : false,
            "attribute3"        : customEventJSON,
    ]

    return ppRow
}