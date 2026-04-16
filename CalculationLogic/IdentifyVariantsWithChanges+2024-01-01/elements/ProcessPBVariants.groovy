if (api.isInputGenerationExecution()) return

def idxByContract = api.local.idxByContract as Map
def idxByPricelist = api.local.idxByPricelist as Map

def variants = out.FindPBVariants

List affectedVariants = []

def mats, parts, effectiveDate
variants?.each { v ->

    mats = libs.DashboardLibrary.Calculations.findPossibleMaterialsForVariants(v)

    idxByContract.each { key, set ->
        set?.each { dk ->
            if (matchDkWithVariant(dk as String, v, mats)) {
                parts = key.tokenize('|')
                effectiveDate = parts.size() > 3 ? parts[3] : null
                affectedVariants.add(v.key1 + "|" + effectiveDate)
            }
        }
    }

    idxByPricelist.each { key, set ->
        set?.each { dk ->
            if (matchDkWithVariant(dk as String, v, mats)) {
                parts = key.tokenize('|')
                effectiveDate = parts.size() > 2 ? parts[2] : null
                affectedVariants.add(v.key1 + "|" + effectiveDate)
            }
        }
    }
}

return affectedVariants

def matchDkWithVariant(String dk, v, List mats) {
    def split = dk.split("\\|")

    def cn = split[0]
    def cl = split[1]
    def mat = split[2]
    def soldTo = split[3]
    def shipTo = split[4]
    def div = split[5]
    def salesOrg = split[6]
    def plt = split[7]
    def plant = split[8]
    def salesPerson = split[9]

    if (v.Contract && !(v.Contract as List).contains(cn)) return false
    if (v.ContractLine && !(v.ContractLine  as List).contains(cl)) return false
    if (mats && !mats.contains(mat)) return false
    if (v.SoldTo && !(v.SoldTo as List).contains(soldTo)) return false
    if (v.ShipTo && !(v.ShipTo as List).contains(shipTo)) return false
    if (v.Division && v.Division != div) return false
    if (v.SalesOrg && !(v.SalesOrg as List).contains(salesOrg)) return false
    if (v.Pricelist && !(v.Pricelist as List).contains(plt)) return false
    if (v.Plant && !(v.Plant as List).contains(plant)) return false
    if (v.SalesPerson && !(v.SalesPerson as List).contains(salesPerson)) return false

    return true
}