if (api.isInputGenerationExecution()) return

def quotesToExpire = out.GetQuotesToExpire

HashSet<String> salesPersonsIds = quotesToExpire?.salesPersonsIds?.flatten()?.toSet() ?: new HashSet<>()
Map<String, String> salesPersonsEmails = api.stream("C", null, ["customerId", "attribute3"], Filter.in("customerId", salesPersonsIds))
        ?.withCloseable {
            it.collectEntries { [(it.customerId): it.attribute3] }
        }

quotesToExpire.each { quote ->
    quote?.salesPersonsIds?.collect { salesPersonsEmails[it] }?.each { email ->
        if (email) {
            api.sendEmail(email as String, "Expired quote ${quote?.uniqueName}", "Quote ${quote?.uniqueName}-${quote?.label} is now expired")
        }
    }
    api.update("Q", [
            "uniqueName"                    : quote?.uniqueName,
            "attributeExtension___Expired"  : "Yes"
    ])
}