def rows = input["rows"]
def sorting = input["sorting"]
def sortedRows = rows

if (sorting == libs.DashboardConstantsLibrary.PLTDashboard.SPS_SORT) {
    sortedRows = rows?.sort (false){ a, b ->
        a?.brand <=> b?.brand ?: a?.ph1 <=> b?.ph1 ?: a?.ph2 <=> b?.ph2 ?: a?.ph3 <=> b?.ph3 ?: a?.ph4 <=> b?.ph4 ?: a?.materialDescription <=> b?.materialDescription ?: a?.moq <=> b?.moq
    }
} else if (sorting == libs.DashboardConstantsLibrary.PLTDashboard.PB_SORT) {
    sortedRows = rows?.sort (false){ a, b ->
        a?.brand <=> b?.brand ?: a?.ph1 <=> b?.ph1 ?: a?.ph2 <=> b?.ph2 ?: a?.ph3 <=> b?.ph3 ?: a?.ph4 <=> b?.ph4 ?: a?.materialDescription <=> b?.materialDescription ?: a?.moq <=> b?.moq
    }
}

api.local.sortedRows = sortedRows

api.local.pricingDate = input["pricingDate"]
api.local.title = input["title"]
api.local.dashboardFooter = input["footer"]

def shouldShowItemsIncluded = input["hasItemsIncluded"]
def shouldShowLegacy = input["hasLegacyPartNo"]

api.local.showAllColumns = shouldShowItemsIncluded && shouldShowLegacy
api.local.showItemsIncluded = shouldShowItemsIncluded && !shouldShowLegacy
api.local.showLegacy = !shouldShowItemsIncluded && shouldShowLegacy
api.local.showNothing = !shouldShowItemsIncluded && !shouldShowLegacy