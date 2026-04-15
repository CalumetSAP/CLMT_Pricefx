def fields = ["customerId", "name", "attribute7", "attribute5"]

def customerIds = api.stream("C", "customerId", fields, *[])?.withCloseable { it.collectEntries{entry -> [(entry.customerId): entry]} }

return customerIds