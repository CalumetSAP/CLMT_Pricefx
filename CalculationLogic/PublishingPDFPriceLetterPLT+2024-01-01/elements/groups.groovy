def finalRows = api.local.finalRows
def orderedKeys = finalRows.keySet().sort()

def finalList = []
orderedKeys?.each { key ->
    finalList?.add([
            groupTitle: key,
            rows      : finalRows?.get(key)
    ])
}

return finalList ?: []