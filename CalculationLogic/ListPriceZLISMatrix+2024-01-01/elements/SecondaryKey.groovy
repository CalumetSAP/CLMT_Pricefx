def secKey = []

def salesOrgs = api.global.salesOrgs
def sku = api.local.sku

for (salesOrg in salesOrgs) {
//    if (out.LoadZLIS.get([sku, salesOrg]) || out.LoadZLISCR.get([salesOrg, sku])) {
        secKey.add(salesOrg)
//    }
}

return secKey?.unique()