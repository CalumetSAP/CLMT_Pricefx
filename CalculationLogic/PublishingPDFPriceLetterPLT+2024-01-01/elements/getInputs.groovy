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
api.local.showJobbers = input["showJobbers"]
api.local.title = input["title"]
api.local.dashboardFooter = input["footer"]

def shouldShowItemsIncluded = input["hasItemsIncluded"]
def shouldShowLegacy = input["hasLegacyPartNo"]
def shouldShowJobbers = input["showJobbers"] && input["hasJobbers"]

api.local.showAllColumns = shouldShowItemsIncluded && shouldShowLegacy && shouldShowJobbers
api.local.dontShowItemsIncluded = !shouldShowItemsIncluded && shouldShowLegacy && shouldShowJobbers
api.local.dontShowLegacy = shouldShowItemsIncluded && !shouldShowLegacy && shouldShowJobbers
api.local.dontShowJobbers = shouldShowItemsIncluded && shouldShowLegacy && !shouldShowJobbers
api.local.showOnlyItemsIncluded = shouldShowItemsIncluded && !shouldShowLegacy && !shouldShowJobbers
api.local.showOnlyLegacy = !shouldShowItemsIncluded && shouldShowLegacy && !shouldShowJobbers
api.local.showOnlyJobbers = !shouldShowItemsIncluded && !shouldShowLegacy && shouldShowJobbers
api.local.showNothing = !shouldShowItemsIncluded && !shouldShowLegacy && !shouldShowJobbers

api.local.currency = input["currency"]