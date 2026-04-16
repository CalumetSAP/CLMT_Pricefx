def filter = [
        Filter.equal("attribute1", "ZC15")
]
def fields = ["customerId", "name"]
def salesPersonMap = api.stream("C", null, fields, *filter)?.withCloseable { it.collectEntries {
    [(it.customerId): it.customerId + (it.name ? " - " + it.name : "")]
} }

return salesPersonMap