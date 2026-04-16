if (api.isInputGenerationExecution()) return

def products = api.global.products
def hierarchies = api.global.productHierarchies

def ph1, ph2, ph3, ph4
def desc1, desc2, desc3, desc4
def loader = []
products?.each { sku, values ->
    ph1 = values.PH1 ?: null
    ph2 = ph1 && values.PH2 ? ph1 + values.PH2 : null
    ph3 = ph2 && values.PH3 ? ph2 + values.PH3 : null
    ph4 = ph3 && values.PH4 ? ph3 + values.PH4 : null

    desc1 = hierarchies?.get(ph1)
    desc2 = hierarchies?.get(ph2)
    desc3 = hierarchies?.get(ph3)
    desc4 = hierarchies?.get(ph4)

    loader.add([
            sku: sku,
            attribute14: ph1 ?: null,
            attribute15: desc1 ?: null,
            attribute16: ph2 ?: null,
            attribute17: desc2 ?: null,
            attribute18: ph3 ?: null,
            attribute19: desc3 ?: null,
            attribute20: ph4 ?: null,
            attribute21: desc4 ?: null,
    ])
}

api.addOrUpdate("P", loader)

return null