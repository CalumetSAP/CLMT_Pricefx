import net.pricefx.domain.ProductGroup

final constants = libs.DashboardConstantsLibrary.PLTDashboard

def configurator = out.Filters
def inputMap = configurator?.get(constants.MATERIAL_INPUT_KEY)
def selectedBrands = configurator?.get(constants.BRAND_INPUT_KEY) ?: []

def materials = []
if (inputMap != null) {
    def pg = ProductGroup.fromMap(inputMap)
    materials = api.getSkusFromProductGroup(pg)
}

def materialByBrand = []
if (selectedBrands) {
    def filter = Filter.in("attribute2", selectedBrands)
    materialByBrand = api.stream("P", "sku", ["sku"], true, filter)?.withCloseable {
        it.collect { it.sku }
    }
}

api.local.selectedMaterials = materials && materialByBrand ? materials.intersect(materialByBrand) : materials + materialByBrand