if (api.isInputGenerationExecution()) return

def material = api.global.materialList ?: []
if (!material) return

def qapi = api.queryApi()
def t1 = qapi.tables().products()
def fields = [
        t1.sku(),
        t1.label(),
        t1.unitOfMeasure(),
        t1.BrandCode.as("attribute2"),
        t1."Attribute 4".as("attribute4"),
        t1.OldMaterialNumber.as("attribute12"),
        t1."Attribute 14".as("attribute14"),
        t1."Attribute 16".as("attribute16"),
        t1."Attribute 18".as("attribute18")
]

api.global.products = qapi.source(t1, fields, t1.sku().in(material)).stream { it.collect { it } }

return null
