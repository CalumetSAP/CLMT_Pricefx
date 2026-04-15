def filters = [
        Filter.isNotNull("attribute2"),
        Filter.notEqual("attribute2", "")
]

def brands = api.stream("P", "attribute2", ["attribute2", "attribute13"], true, *filters)?.withCloseable {
    it.collectEntries{ [(it.attribute2): it.attribute2 + " - " + it.attribute13] }
}

return brands ?: [:]